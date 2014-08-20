package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.activity.CancelableTask;
import gt.high5.activity.fragment.RecordDetailFragment.BUNDLE_KEYS;
import gt.high5.database.table.Total;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author GT
 * 
 *         fragment showing the overall infomation of a package
 */
public class TotalDetailPagerFragment extends Fragment implements
		CancelableTask {

	private static final int SPLITTER_SIZE = 1;

	private RelativeLayout mNameWrapper = null;
	private RelativeLayout mCountWrapper = null;
	private ImageView mAppIconImage = null;
	private TextView mAppNameText = null;
	private TextView mCountText = null;
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
		mLoadingBar = (ProgressBar) root
				.findViewById(R.id.total_detail_view_load_progress_bar);
		mErrorImage = (ImageView) root
				.findViewById(R.id.total_detail_view_error_image);
		mNameWrapper = (RelativeLayout) root
				.findViewById(R.id.total_detail_name_wrapper);
		mCountWrapper = (RelativeLayout) root
				.findViewById(R.id.total_detail_count_wrapper);
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
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		loadGraph();
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
	
		public AppInfo(Drawable icon, String name) {
			mIcon = icon;
			mName = name;
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
			mNameWrapper.setVisibility(View.GONE);
			mCountWrapper.setVisibility(View.GONE);
			mErrorImage.setVisibility(View.GONE);
			mLoadingBar.setVisibility(View.VISIBLE);
			for (View v : mSplitters) {
				v.setVisibility(View.GONE);
			}
	
			mLoader = this;
		}
	
		@Override
		protected AppInfo doInBackground(Void... arg0) {
			try {
				PackageManager manager = getActivity().getPackageManager();
				ApplicationInfo info = manager.getApplicationInfo(
						mTotal.getName(), PackageManager.GET_META_DATA);
				return new AppInfo(
						manager.getApplicationIcon(mTotal.getName()), manager
								.getApplicationLabel(info).toString());
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
		if (null != result) {
			mAppIconImage.setImageDrawable(result.getIcon());
			mAppNameText.setText(result.getName());
			mCountText.setText(mTotal.getCount() + "");
			for (View v : mSplitters) {
				v.setVisibility(View.VISIBLE);
			}
			mNameWrapper.setVisibility(View.VISIBLE);
			mCountWrapper.setVisibility(View.VISIBLE);

		} else {
			mErrorImage.setVisibility(View.VISIBLE);
		}
	}
}
