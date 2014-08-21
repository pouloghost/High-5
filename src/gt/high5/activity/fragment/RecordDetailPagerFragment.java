package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.activity.CancelableTask;
import gt.high5.activity.fragment.RecordDetailFragment.BUNDLE_KEYS;
import gt.high5.chart.core.DataFiller;
import gt.high5.chart.core.FillContext;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.table.Total;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.androidplot.pie.PieChart;
import com.androidplot.xy.XYPlot;

/**
 * @author GT
 * 
 *         a fragment containing a chart and a spinner for selecting type of
 *         chart
 */
public class RecordDetailPagerFragment extends Fragment implements
		CancelableTask {

	private Spinner mGraphTypeSpinner = null;
	private ProgressBar mLoadingBar = null;
	private XYPlot mXyChart = null;
	private PieChart mPieChart = null;

	private ImageView mErrorImage = null;
	// this chart display the mRecordType data of mTotal
	private Total mTotal = null;

	private DataFiller mFiller = null;

	private Class<? extends RecordTable> mRecordType = null;

	private AsyncGraphDrawer mDrawer = null;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mTotal = args.getParcelable(BUNDLE_KEYS.TOTAL.toString());
		try {
			mRecordType = (Class<? extends RecordTable>) Class.forName(args
					.getString(BUNDLE_KEYS.CLASS.toString()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.record_detail_pager_fragment,
				container, false);
		mGraphTypeSpinner = (Spinner) root
				.findViewById(R.id.record_detail_view_graph_type_spinner);
		mLoadingBar = (ProgressBar) root
				.findViewById(R.id.record_detail_view_load_progress_bar);
		mXyChart = (XYPlot) root.findViewById(R.id.record_detail_view_xy_chart);
		mPieChart = (PieChart) root
				.findViewById(R.id.record_detail_view_pie_chart);
		mErrorImage = (ImageView) root
				.findViewById(R.id.record_detail_view_error_image);

		mGraphTypeSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						loadGraph();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		try {
			mFiller = DatabaseAccessor.getAccessor(
					getActivity().getApplicationContext(), R.xml.tables)
					.getDataFiller(mRecordType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setEntries();

		return root;
	}

	// replacement for onResume, this is actually functioning as onResume should
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// the chart are overlapping each other
		if (isVisibleToUser
				&& (mXyChart.getVisibility() == mPieChart.getVisibility())) {
			loadGraph();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	public void setData(Total total, Class<? extends RecordTable> clazz) {
		mTotal = total;
		mRecordType = clazz;
		loadGraph();
	}

	@Override
	public void cancel() {
		if (isCancelable()) {
			mDrawer.cancel(true);
			onFinishLoading(null);
		}
	}

	@Override
	public boolean isCancelable() {
		return null != mDrawer;
	}

	/**
	 * @author GT
	 * 
	 *         async task for filling up the chart view
	 */
	class AsyncGraphDrawer extends AsyncTask<FillContext, Void, FillContext> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mXyChart.setVisibility(View.GONE);
			mPieChart.setVisibility(View.GONE);
			mErrorImage.setVisibility(View.GONE);
			mLoadingBar.setVisibility(View.VISIBLE);

			mDrawer = this;
		}

		@Override
		protected FillContext doInBackground(FillContext... arg0) {
			FillContext fillContext = arg0[0];
			if (null != mFiller) {
				mFiller.fillView(fillContext);
			} else {
				fillContext.setView2Show(null);
			}
			return fillContext;
		}

		@Override
		protected void onPostExecute(FillContext result) {
			super.onPostExecute(result);
			onFinishLoading(result);
		}

	}

	private void setEntries() {
		if (null != mFiller) {
			int[] ids = mFiller.getEntryIds();
			String[] entries = new String[ids.length];
			Resources resources = getActivity().getResources();
			for (int i = 0; i < ids.length; ++i) {
				entries[i] = resources.getString(ids[i]);
			}
			mGraphTypeSpinner.setAdapter(new ArrayAdapter<String>(
					getActivity(), R.layout.record_detail_pager_spinner_item,
					R.id.record_detail_spinner_text, entries));
		}
	}

	private void loadGraph() {
		if (null != mTotal && null != mGraphTypeSpinner) {
			int id = (int) mGraphTypeSpinner.getSelectedItemId();
			FillContext context = new FillContext(id, mXyChart, mPieChart,
					getActivity().getApplicationContext(), mTotal, mRecordType);
			new AsyncGraphDrawer().execute(context);
		}
	}

	private void onFinishLoading(FillContext result) {
		mLoadingBar.setVisibility(View.GONE);
		mDrawer = null;
		// if view not filled properly the reference in fillContext will be
		// set to be null
		if (null != result && null != result.getView2Show()) {
			result.getView2Show().setVisibility(View.VISIBLE);
		} else {
			mErrorImage.setVisibility(View.VISIBLE);
		}
	}
}
