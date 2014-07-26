package gt.high5.activity;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.tables.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ToggleButton;

/**
 * @author GT
 * 
 *         Ignore list manage activity
 */

public class MainActivity extends Activity {

	public static final String LOG_TAG = "GT";

	private static boolean debugging = false;

	private static enum KEYS {
		ICON, PACKAGE, NAME, IGNORED
	}

	private ListView mIgnoreList = null;
	private SimpleAdapter mAdapter = null;
	private ProgressDialog mDialog = null;
	private PackageManager mPackageManager = null;
	private DatabaseAccessor mAccessor = null;

	private HashSet<String> mIgnoredSet = new HashSet<String>();
	private ArrayList<HashMap<String, Object>> mDataList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.ignore_list_layout);

		mIgnoreList = (ListView) findViewById(R.id.ignore_list);

	}

	@Override
	protected void onResume() {
		super.onResume();

		new LoadDataTask().execute();
	}

	/**
	 * load package list and ignore list
	 * 
	 * mDataList will be modified
	 * 
	 * @return the data list for adapter
	 */
	private ArrayList<HashMap<String, Object>> loadData() {
		// load all packages
		List<ApplicationInfo> infos = mPackageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		mDataList = new ArrayList<HashMap<String, Object>>();

		for (ApplicationInfo info : infos) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put(KEYS.ICON.toString(), info.packageName);
			data.put(KEYS.NAME.toString(),
					mPackageManager.getApplicationLabel(info));
			data.put(KEYS.PACKAGE.toString(), info.packageName);
			data.put(KEYS.IGNORED.toString(), info.packageName);

			mDataList.add(data);
		}
		// load ignore list
		Ignore ignoreQuery = new Ignore();
		ArrayList<Table> ignores = mAccessor.R(ignoreQuery);
		if (null != ignores) {
			for (Table ignore : ignores) {
				mIgnoredSet.add(((Ignore) ignore).getName());
			}
		}

		return mDataList;
	}

	/**
	 * set data to listview
	 * 
	 * @param data
	 *            data loaded from {@link loadData()}
	 */
	private void setData(ArrayList<HashMap<String, Object>> data) {
		// set adapter
		String[] from = { KEYS.ICON.toString(), KEYS.NAME.toString(),
				KEYS.PACKAGE.toString(), KEYS.IGNORED.toString() };
		int[] to = { R.id.ignore_list_icon_image, R.id.ignore_list_app_name,
				R.id.ignore_list_package_name, R.id.ignore_list_ignore_toggle };

		mAdapter = new SimpleAdapter(getApplicationContext(), data,
				R.layout.ignore_list_item, from, to);
		mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (R.id.ignore_list_icon_image == view.getId()
						&& view instanceof ImageView) {
					new AsyncImageTask().execute(new Object[] { data, view });
					return true;
				} else if (R.id.ignore_list_ignore_toggle == view.getId()
						&& view instanceof ToggleButton) {
					((ToggleButton) view).setChecked(mIgnoredSet.contains(data));
					return true;
				}
				return false;
			}
		});

		mIgnoreList.setAdapter(mAdapter);
		// set onclicklistener
		mIgnoreList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (id != -1) {
							int pos = (int) id;
							// get package name
							String name = (String) mDataList.get(pos).get(
									KEYS.PACKAGE.toString());
							Ignore ignoreQuery = new Ignore();
							ignoreQuery.setName(name);
							// change ignore status
							ToggleButton toggle = (ToggleButton) view
									.findViewById(R.id.ignore_list_ignore_toggle);
							if (toggle.isChecked()) {// in database
								mAccessor.D(ignoreQuery);
								mIgnoredSet.remove(name);
							} else {
								mAccessor.C(ignoreQuery);
								mIgnoredSet.add(name);
							}
							toggle.setChecked(mIgnoredSet.contains(name));
							// update
							// mAdapter.notifyDataSetChanged();
						}
					}
				});
	}

	public static boolean isDebugging() {
		return debugging;
	}

	public static void setDebugging(boolean debugging) {
		MainActivity.debugging = debugging;
	}

	/**
	 * @author GT
	 * 
	 *         asynctask for load data
	 * 
	 *         a progress dialog will be showing when loading
	 * 
	 *         all implementation will use {@link loadData()} and {@link
	 *         setData()}
	 */
	class LoadDataTask extends
			AsyncTask<Void, Integer, ArrayList<HashMap<String, Object>>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = new ProgressDialog(MainActivity.this);
			mDialog.show();

			mPackageManager = getPackageManager();
			mAccessor = DatabaseAccessor.getAccessor(getApplicationContext(),
					R.xml.tables);
		}

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(
				Void... params) {
			return loadData();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
			super.onPostExecute(result);
			mDialog.dismiss();
			setData(result);
		}
	}

	/**
	 * @author GT
	 * 
	 *         async task for loading app icon from packagemanager and set to
	 *         imageview
	 * 
	 * @param Object
	 *            [0] String the package name for retrieving app icon
	 * @param Object
	 *            [1] ImageView the image view for showing app icon
	 */
	class AsyncImageTask extends AsyncTask<Object, Void, Bitmap> {

		private ImageView imageView = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(Object... arg0) {
			String packageName = (String) arg0[0];
			imageView = (ImageView) arg0[1];

			try {
				return ((BitmapDrawable) mPackageManager
						.getApplicationIcon(packageName)).getBitmap();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (null != imageView && null != result) {
				imageView.setImageBitmap(result);
			}
		}

	}
}