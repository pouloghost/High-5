package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.activity.AsyncImageTask;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class TotalListFragment extends Fragment {
	private static enum KEYS {
		ICON, PACKAGE, NAME
	}

	private ListView mTotalList = null;
	private SimpleAdapter mAdapter = null;
	private ArrayList<HashMap<String, Object>> mDataList = null;
	private ArrayList<Table> mTotals = null;

	private ProgressDialog mDialog = null;

	private PackageManager mPackageManager = null;

	private DatabaseAccessor mAccessor = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.total_list_layout, container,
				false);
		mTotalList = (ListView) view.findViewById(R.id.total_list);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		new LoadDataTask().execute();
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
			mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	
				@Override
				public void onCancel(DialogInterface dialog) {
					LoadDataTask.this.cancel(true);
				}
			});
			mDialog.show();
	
			mAccessor = DatabaseAccessor.getAccessor(getActivity()
					.getApplicationContext(), R.xml.tables);
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

	/**
	 * load package list and ignore list
	 * 
	 * mDataList will be modified
	 * 
	 * @return the data list for adapter
	 */
	private ArrayList<HashMap<String, Object>> loadData() {
		// load all records
		Total query = new Total();
		ArrayList<Table> totals = mAccessor.R(query);

		mDataList = new ArrayList<HashMap<String, Object>>();
		if (null != totals) {
			mTotals = totals;
			ApplicationInfo info = null;
			for (Table total : totals) {
				try {
					info = mPackageManager.getApplicationInfo(
							((Total) total).getName(),
							PackageManager.GET_META_DATA);
					HashMap<String, Object> data = new HashMap<String, Object>();
					data.put(KEYS.ICON.toString(), info.packageName);
					data.put(KEYS.NAME.toString(),
							mPackageManager.getApplicationLabel(info));
					data.put(KEYS.PACKAGE.toString(), info.packageName);

					mDataList.add(data);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
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
				KEYS.PACKAGE.toString() };
		int[] to = { R.id.total_list_icon_image, R.id.total_list_app_name,
				R.id.total_list_package_name };
		mAdapter = new SimpleAdapter(getActivity().getApplicationContext(),
				data, R.layout.total_list_item, from, to);

		// view binder to load icon and checkbox using package name
		mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (R.id.total_list_icon_image == view.getId()
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
				}
				return false;
			}
		});

		mTotalList.setAdapter(mAdapter);
		// set onclicklistener
		mTotalList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (id != -1) {
							int pos = (int) id;
							Bundle args = new Bundle();
							args.putParcelable(
									RecordDetailFragment.BUNDLE_KEYS.TOTAL
											.toString(), (Total) mTotals
											.get(pos));
							args.putString(
									RecordDetailFragment.BUNDLE_KEYS.LABEL
											.toString(), (String) mDataList
											.get(pos).get(KEYS.NAME.toString()));
							// transaction
							RecordDetailFragment recordDetail = new RecordDetailFragment();
							recordDetail.setArguments(args);

							((FragmentActivity) getActivity())
									.getSupportFragmentManager()
									.beginTransaction()
									.replace(R.id.container, recordDetail)
									.addToBackStack(null).commit();
						}
					}
				});
	}
}
