package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.core.service.IgnoreSetService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class IgnoreListManageFragment extends Fragment {

	private static enum KEYS {
		ICON, PACKAGE, NAME, IGNORED
	}

	private ListView mIgnoreList = null;
	private SimpleAdapter mAdapter = null;
	private ArrayList<HashMap<String, Object>> mDataList = null;

	private ProgressDialog mDialog = null;

	private PackageManager mPackageManager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.ignore_list_layout, container,
				false);
		mIgnoreList = (ListView) view.findViewById(R.id.ignore_list);

		return view;
	}

	@Override
	public void onResume() {
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
		mAdapter = new SimpleAdapter(getActivity().getApplicationContext(),
				data, R.layout.ignore_list_item, from, to);

		final HashSet<String> ignoreSet = IgnoreSetService.getIgnoreSetService(
				getActivity().getApplicationContext()).getIgnoreSet();
		// view binder to load icon and checkbox using package name
		mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (R.id.ignore_list_icon_image == view.getId()
						&& view instanceof ImageView) {

					AsyncImageTask task = (AsyncImageTask) view.getTag();
					if (null != task) {
						task.cancel(true);
					}
					task = new AsyncImageTask(mPackageManager);
					task.execute(new Object[] { data, view });
					// task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					// new Object[] { data, view });
					view.setTag(task);

					return true;
				} else if (R.id.ignore_list_ignore_toggle == view.getId()
						&& view instanceof ToggleButton) {
					if (null != ignoreSet) {
						((ToggleButton) view).setChecked(ignoreSet
								.contains(data));
					}
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
							ToggleButton toggle = (ToggleButton) view
									.findViewById(R.id.ignore_list_ignore_toggle);
							HashSet<String> ignoreSet = IgnoreSetService
									.getIgnoreSetService(
											getActivity()
													.getApplicationContext())
									.update(name, toggle.isChecked());
							toggle.setChecked(ignoreSet.contains(name));
							// update
							// mAdapter.notifyDataSetChanged();
						}
					}
				});
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
			mDialog = new ProgressDialog(getActivity());
			mDialog.show();

			mPackageManager = getActivity().getPackageManager();
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

}