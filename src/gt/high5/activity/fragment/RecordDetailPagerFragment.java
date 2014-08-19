package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.activity.fragment.RecordDetailFragment.BUNDLE_KEYS;
import gt.high5.chart.core.DataFiller;
import gt.high5.chart.core.DataFiller.CHART_TYPE;
import gt.high5.chart.core.FillContext;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.table.Total;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

	/**
	 * spinner id to chart type mapping
	 */
	private static final CHART_TYPE[] ID2TYPE = { CHART_TYPE.PIE,
			CHART_TYPE.BAR, CHART_TYPE.LINE };

	private Spinner mGraphTypeSpinner = null;
	private ProgressBar mLoadingBar = null;
	private XYPlot mXyChart = null;
	private PieChart mPieChart = null;
	private ImageView mErrorImage = null;

	// this chart display the mRecordType data of mTotal
	private Total mTotal = null;
	private Class<? extends RecordTable> mRecordType = null;

	/**
	 * spinner id to view pointer mapping
	 */
	private View[] mId2Views = null;

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

		mId2Views = new View[] { mPieChart, mXyChart, mXyChart };
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		loadGraph();
	}

	public void setData(Total total, Class<? extends RecordTable> clazz) {
		mTotal = total;
		mRecordType = clazz;
		loadGraph();
	}

	private void loadGraph() {
		if (null != mTotal && null != mGraphTypeSpinner) {
			int id = (int) mGraphTypeSpinner.getSelectedItemId();
			FillContext context = new FillContext(ID2TYPE[id], mId2Views[id],
					getActivity().getApplicationContext(), mTotal, mRecordType);
			new AsyncGraphDrawer().execute(context);
		}
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
			try {
				DataFiller filler = DatabaseAccessor.getAccessor(
						getActivity().getApplicationContext(), R.xml.tables)
						.getDataFiller(mRecordType);
				filler.fillView(fillContext);
			} catch (Exception e) {
				fillContext.setView(null);
				e.printStackTrace();
			}
			return fillContext;
		}

		@Override
		protected void onPostExecute(FillContext result) {
			super.onPostExecute(result);
			onFinishLoading(result);
		}

	}

	private void onFinishLoading(FillContext result) {
		mLoadingBar.setVisibility(View.GONE);
		mDrawer = null;
		// if view not filled properly the reference in fillContext will be
		// set to be null
		if (null != result && null != result.getView()) {
			result.getView().setVisibility(View.VISIBLE);
		} else {
			mErrorImage.setVisibility(View.VISIBLE);
		}
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
}
