package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.activity.AsyncImageTask;
import gt.high5.core.service.ReadService;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TotalListFragment extends Fragment {
	private static enum KEYS {
		ICON, POSSIBILITY, NAME, _ID
	}

	private ListView mTotalList = null;
	private SimpleAdapter mAdapter = null;
	private ArrayList<HashMap<String, Object>> mDataList = null;
	private SparseArray<Total> mTotals = new SparseArray<Total>();
	private View mHeader = null;

	private ProgressDialog mDialog = null;
	private Spinner mSortSpinner = null;

	private PackageManager mPackageManager = null;

	private LoadDataTask mTask = null;

	private Comparator<?>[] mComparators = {
			new Comparator<HashMap<String, Object>>() {

				@Override
				public int compare(HashMap<String, Object> a,
						HashMap<String, Object> b) {
					return (Integer) a.get(KEYS._ID.toString())
							- (Integer) b.get(KEYS._ID.toString());
				}
			}, new Comparator<HashMap<String, Object>>() {

				@Override
				public int compare(HashMap<String, Object> a,
						HashMap<String, Object> b) {
					Total ta = mTotals
							.get((Integer) a.get(KEYS._ID.toString()));
					Total tb = mTotals
							.get((Integer) b.get(KEYS._ID.toString()));
					return (int) (tb.getTimestamp() - ta.getTimestamp());
				}
			}, new Comparator<HashMap<String, Object>>() {

				@Override
				public int compare(HashMap<String, Object> a,
						HashMap<String, Object> b) {
					float p0 = (Float) a.get(KEYS.POSSIBILITY.toString());
					float p1 = (Float) b.get(KEYS.POSSIBILITY.toString());
					return p1 > p0 ? 1 : p1 == p0 ? 0 : -1;
				}
			}, new Comparator<HashMap<String, Object>>() {

				@Override
				public int compare(HashMap<String, Object> a,
						HashMap<String, Object> b) {
					return ((String) a.get(KEYS.NAME.toString()))
							.compareTo((String) b.get(KEYS.NAME.toString()));
				}
			}, new Comparator<HashMap<String, Object>>() {

				@Override
				public int compare(HashMap<String, Object> a,
						HashMap<String, Object> b) {
					Total ta = mTotals
							.get((Integer) a.get(KEYS._ID.toString()));
					Total tb = mTotals
							.get((Integer) b.get(KEYS._ID.toString()));
					return (int) (tb.getCount() - ta.getCount());
				}
			} };
	private String[] mEntries;

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
		mSortSpinner = (Spinner) view
				.findViewById(R.id.total_list_sort_spinner);

		mSortSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						// after load done
						if (null != mAdapter) {
							Collections
									.sort(mDataList,
											(Comparator<HashMap<String, Object>>) mComparators[(int) id]);
							mAdapter.notifyDataSetChanged();
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
		mEntries = getActivity().getResources().getStringArray(
				R.array.total_list_sort_spinner);
		mSortSpinner.setAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_item, R.id.spinner_text, mEntries));
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (null == mTask) {
			mTask = new LoadDataTask();
			mTask.execute();
		}
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

			mPackageManager = getActivity().getPackageManager();
		}

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(
				Void... params) {
			return loadData();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
			super.onPostExecute(result);
			Collections
					.sort(mDataList,
							(Comparator<HashMap<String, Object>>) mComparators[(int) mSortSpinner
									.getSelectedItemId()]);
			mDialog.dismiss();
			setData(result);
			mTask = null;
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
		Context context = getActivity().getApplicationContext();

		List<Table> totals = ReadService.getReadService(context).getAll();

		mDataList = new ArrayList<HashMap<String, Object>>();
		if (null != totals) {
			mTotals.clear();
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
					data.put(KEYS.POSSIBILITY.toString(),
							Float.valueOf(((Total) total).getPossibility()));
					// for sorting
					data.put(KEYS._ID.toString(), total.getId());
					mDataList.add(data);

					mTotals.put(total.getId(), (Total) total);
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
		addHeader();
		// set adapter
		String[] from = { KEYS.ICON.toString(), KEYS.NAME.toString(),
				KEYS.POSSIBILITY.toString() };
		int[] to = { R.id.total_list_icon_image, R.id.total_list_app_name,
				R.id.total_list_possibility };
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
							HashMap<String, Object> mapping = mDataList
									.get(pos);
							args.putParcelable(
									RecordDetailFragment.BUNDLE_KEYS.TOTAL
											.toString(), (Total) mTotals
											.get((Integer) mapping.get(KEYS._ID
													.toString())));
							args.putString(
									RecordDetailFragment.BUNDLE_KEYS.LABEL
											.toString(), (String) mapping
											.get(KEYS.NAME.toString()));
							// transaction
							RecordDetailFragment recordDetail = new RecordDetailFragment();
							recordDetail.setArguments(args);

							getActivity().getFragmentManager()
									.beginTransaction()
									.replace(R.id.container, recordDetail)
									.addToBackStack(null).commit();
						}
					}
				});
	}

	private void addHeader() {
		if (null == mHeader) {
			mHeader = LayoutInflater.from(getActivity()).inflate(
					R.layout.total_list_header, mTotalList, false);
			mTotalList.addHeaderView(mHeader);
		}
		ReadService readService = ReadService.getReadService(getActivity()
				.getApplicationContext());
		((TextView) mHeader.findViewById(R.id.total_list_header_precise))
				.setText("" + readService.getPrecise());
		((TextView) mHeader.findViewById(R.id.total_list_header_recall))
				.setText("" + readService.getRecallRate());
	}
}
