package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.activity.CancelableTask;
import gt.high5.activity.fragment.RecordDetailFragment.BUNDLE_KEYS;
import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.Predictor;
import gt.high5.core.service.RecordService;
import gt.high5.database.model.RecordTable;
import gt.high5.database.parser.TableParser;
import gt.high5.database.table.Total;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author GT
 * 
 *         fragment showing the overall information of a package
 */
public class TotalDetailPagerFragment extends Fragment implements
		CancelableTask {

	private static final int SPLITTER_SIZE = 3;

	private RelativeLayout mNameWrapper = null;
	private RelativeLayout mCountWrapper = null;
	private RelativeLayout mOperationWrapper = null;
	private RelativeLayout mRecordWrapper = null;
	private ImageView mAppIconImage = null;
	private TextView mAppNameText = null;
	private TextView mCountText = null;
	private Button mDeleteButton = null;
	private Button mRecordSwitchButton = null;
	private TableLayout mRecordList = null;

	private RelativeLayout[] mWrappers = null;
	private View[] mSplitters = null;

	private ProgressBar mLoadingBar = null;
	private ImageView mErrorImage = null;

	private Total mTotal = null;

	private AsyncInfoLoader mLoader = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTotal = getArguments().getParcelable(BUNDLE_KEYS.TOTAL.toString());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.total_detail_pager_fragment,
				container, false);
		mAppIconImage = (ImageView) root
				.findViewById(R.id.total_detail_app_icon);
		mAppNameText = (TextView) root.findViewById(R.id.total_detail_app_name);
		mCountText = (TextView) root.findViewById(R.id.total_detail_count);
		mDeleteButton = (Button) root
				.findViewById(R.id.total_detail_operation_delete);
		mRecordSwitchButton = (Button) root
				.findViewById(R.id.total_detail_records_switch);
		mRecordList = (TableLayout) root
				.findViewById(R.id.total_detail_records_list);
		mLoadingBar = (ProgressBar) root
				.findViewById(R.id.total_detail_view_load_progress_bar);
		mErrorImage = (ImageView) root
				.findViewById(R.id.total_detail_view_error_image);
		mNameWrapper = (RelativeLayout) root
				.findViewById(R.id.total_detail_name_wrapper);
		mCountWrapper = (RelativeLayout) root
				.findViewById(R.id.total_detail_count_wrapper);
		mOperationWrapper = (RelativeLayout) root
				.findViewById(R.id.total_detail_operation_wrapper);
		mRecordWrapper = (RelativeLayout) root
				.findViewById(R.id.total_detail_records_wrapper);
		mWrappers = new RelativeLayout[] { mNameWrapper, mCountWrapper,
				mOperationWrapper, mRecordWrapper };
		// for more segments
		mSplitters = new View[SPLITTER_SIZE];
		String prefix = getResources().getResourcePackageName(
				R.id.total_detail_app_icon)
				+ ":"
				+ getResources()
						.getResourceTypeName(R.id.total_detail_app_icon)
				+ "/total_detail_splitter_";
		for (int i = 0; i < SPLITTER_SIZE; ++i) {
			mSplitters[i] = root.findViewById(getResources().getIdentifier(
					prefix + i, null, null));
		}
		// controllers
		mDeleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (null != mTotal) {
					new DeleteAllTask().execute(mTotal.getName());
				}
			}
		});
		mRecordSwitchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (View.GONE == mRecordList.getVisibility()) {
					mRecordSwitchButton.setText(R.string.total_detail_hide);
					mRecordList.setVisibility(View.VISIBLE);
				} else {
					mRecordSwitchButton.setText("Possibility:"
							+ mTotal.getPossibility());
					mRecordList.setVisibility(View.GONE);
				}
			}
		});
		mRecordSwitchButton.setText("Possibility:" + mTotal.getPossibility());
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isCancelable()) {
			loadGraph();
		}
	}

	@Override
	public void cancel() {
		if (isCancelable()) {
			mLoader.cancel(true);
			onFinishLoading(null);
		}
	}

	@Override
	public boolean isCancelable() {
		return null != mLoader;
	}

	/**
	 * @author GT
	 * 
	 *         wrapper for icon and label
	 */
	class AppInfo {
		private Drawable mIcon = null;
		private String mName = null;
		private Collection<RecordTable> mRecords = null;

		public AppInfo(Drawable icon, String name,
				Collection<RecordTable> collection) {
			mIcon = icon;
			mName = name;
			mRecords = collection;
		}

		public Drawable getIcon() {
			return mIcon;
		}

		public void setIcon(Drawable mIcon) {
			this.mIcon = mIcon;
		}

		public String getName() {
			return mName;
		}

		public void setName(String mName) {
			this.mName = mName;
		}

		public Collection<RecordTable> getRecords() {
			return mRecords;
		}

		public void setRecords(ArrayList<RecordTable> mRecords) {
			this.mRecords = mRecords;
		}

	}

	class DeleteAllTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoading();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				RecordService.getRecordService(
						getActivity().getApplicationContext()).removeRecords(
						params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				getActivity().getSupportFragmentManager().popBackStack();
			} else {
				Toast.makeText(getActivity(),
						R.string.total_detail_delete_failed, Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	/**
	 * @author GT
	 * 
	 *         async task for filling up info views
	 */
	class AsyncInfoLoader extends AsyncTask<Void, Void, AppInfo> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoading();

			mLoader = this;
		}

		@Override
		protected AppInfo doInBackground(Void... arg0) {
			try {
				PackageManager manager = getActivity().getPackageManager();
				ApplicationInfo info = manager.getApplicationInfo(
						mTotal.getName(), PackageManager.GET_META_DATA);
				Context context = getActivity().getApplicationContext();
				Predictor predictor = Predictor.getPredictor();
				return new AppInfo(
						manager.getApplicationIcon(mTotal.getName()), manager
								.getApplicationLabel(info).toString(),
						predictor.getRelativeRecords(
								new PredictContext(context), mTotal));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(AppInfo result) {
			super.onPostExecute(result);
			onFinishLoading(result);
		}

	}

	private void showLoading() {
		mErrorImage.setVisibility(View.GONE);
		mLoadingBar.setVisibility(View.VISIBLE);
		for (View v : mSplitters) {
			v.setVisibility(View.GONE);
		}
		for (RelativeLayout l : mWrappers) {
			l.setVisibility(View.GONE);
		}
	}

	private void loadGraph() {
		if (null != mTotal) {
			new AsyncInfoLoader().execute();
		}
	}

	private void onFinishLoading(AppInfo result) {
		mLoadingBar.setVisibility(View.GONE);
		mLoader = null;
		// if view not filled properly the reference in fillContext will be
		// set to be null
		if (null != result && null != result.getRecords()) {
			mAppIconImage.setImageDrawable(result.getIcon());
			mAppNameText.setText(result.getName());
			mCountText.setText(mTotal.getCount() + "");
			for (View v : mSplitters) {
				v.setVisibility(View.VISIBLE);
			}
			for (RelativeLayout l : mWrappers) {
				l.setVisibility(View.VISIBLE);
			}
			fillRecordTable(result.getRecords());
		} else {
			mErrorImage.setVisibility(View.VISIBLE);
		}
	}

	private void fillRecordTable(Collection<RecordTable> records) {
		TableParser tableParser = Predictor.getPredictor()
				.getAccessor(getActivity().getApplicationContext())
				.getTableParser();
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		for (RecordTable record : records) {
			View row = inflater.inflate(
					R.layout.total_detail_pager_record_list_item, mRecordList,
					false);
			((TextView) row.findViewById(R.id.total_detail_record_list_type))
					.setText(tableParser.getTableTitle(record.getClass()));
			((TextView) row.findViewById(R.id.total_detail_record_list_count))
					.setText(":" + record.getCount());
			mRecordList.addView(row);
		}
	}
}
