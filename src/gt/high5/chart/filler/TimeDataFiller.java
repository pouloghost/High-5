package gt.high5.chart.filler;

import gt.high5.R;
import gt.high5.chart.core.DataFiller;
import gt.high5.chart.core.RendererFactory;
import gt.high5.core.predictor.Predictor;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.table.Time;
import gt.high5.database.table.Total;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;
import android.view.View;

public class TimeDataFiller extends DataFiller {

	private final static String[] REGION2TITLE = { "0:00-0:15", "0:15-0:30",
			"0:30-0:45", "0:45-1:00", "1:00-1:15", "1:15-1:30", "1:30-1:45",
			"1:45-2:00", "2:00-2:15", "2:15-2:30", "2:30-2:45", "2:45-3:00",
			"3:00-3:15", "3:15-3:30", "3:30-3:45", "3:45-4:00", "4:00-4:15",
			"4:15-4:30", "4:30-4:45", "4:45-5:00", "5:00-5:15", "5:15-5:30",
			"5:30-5:45", "5:45-6:00", "6:00-6:15", "6:15-6:30", "6:30-6:45",
			"6:45-7:00", "7:00-7:15", "7:15-7:30", "7:30-7:45", "7:45-8:00",
			"8:00-8:15", "8:15-8:30", "8:30-8:45", "8:45-9:00", "9:00-9:15",
			"9:15-9:30", "9:30-9:45", "9:45-10:00", "10:00-10:15",
			"10:15-10:30", "10:30-10:45", "10:45-11:00", "11:00-11:15",
			"11:15-11:30", "11:30-11:45", "11:45-12:00", "12:00-12:15",
			"12:15-12:30", "12:30-12:45", "12:45-13:00", "13:00-13:15",
			"13:15-13:30", "13:30-13:45", "13:45-14:00", "14:00-14:15",
			"14:15-14:30", "14:30-14:45", "14:45-15:00", "15:00-15:15",
			"15:15-15:30", "15:30-15:45", "15:45-16:00", "16:00-16:15",
			"16:15-16:30", "16:30-16:45", "16:45-17:00", "17:00-17:15",
			"17:15-17:30", "17:30-17:45", "17:45-18:00", "18:00-18:15",
			"18:15-18:30", "18:30-18:45", "18:45-19:00", "19:00-19:15",
			"19:15-19:30", "19:30-19:45", "19:45-20:00", "20:00-20:15",
			"20:15-20:30", "20:30-20:45", "20:45-21:00", "21:00-21:15",
			"21:15-21:30", "21:30-21:45", "21:45-22:00", "22:00-22:15",
			"22:15-22:30", "22:30-22:45", "22:45-23:00", "23:00-23:15",
			"23:15-23:30", "23:30-23:45", "23:45-24:00", };
	private List<Table> mData = null;

	@Override
	protected void addFillers() {
		// pie chart
		mFillers.add(new ViewFiller() {
			private DefaultRenderer mRenderer = null;
			private CategorySeries mCategorySeries = null;
			private boolean mResult = false;

			@Override
			public boolean fillView() {
				if (null != mContext) {
					loadData();
					String title = getAccessor().getTableTitle(
							mContext.getRecord());
					mRenderer = RendererFactory.buildPieRenderer(
							mContext.getContext(), getColors(mData.size()));
					mCategorySeries = new CategorySeries(title);
					Time time = null;
					for (Table table : mData) {
						time = (Time) table;
						mCategorySeries.add(REGION2TITLE[time.getRegion()],
								time.getCount());
					}

					mResult = true;
				}
				return mResult;
			}

			@Override
			public View onFinish() {
				View view = null;
				if (mResult) {
					view = ChartFactory.getPieChartView(mContext.getContext(),
							mCategorySeries, mRenderer);
				}
				return view;
			}
		});
		// bar chart
		mFillers.add(new ViewFiller() {
			private XYMultipleSeriesRenderer mRenderer = null;
			private XYMultipleSeriesDataset mDataset = null;
			private boolean mResult = false;

			@Override
			public boolean fillView() {
				if (null != mContext) {
					try {
						loadData();
						String title = getAccessor().getTableTitle(
								mContext.getRecord());
						mRenderer = RendererFactory.buildBarRenderer(
								mContext.getContext(), Color.BLUE);

						addXTitles(mRenderer, 20);

						mDataset = new XYMultipleSeriesDataset();
						mDataset.addSeries(getDataset(title));

						mResult = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return mResult;
			}

			@Override
			public View onFinish() {
				View view = null;
				if (mResult) {
					view = ChartFactory.getBarChartView(mContext.getContext(),
							mDataset, mRenderer, Type.DEFAULT);
				}
				return view;
			}
		});
		// line chart
		mFillers.add(new ViewFiller() {
			private XYMultipleSeriesRenderer mRenderer = null;
			private XYMultipleSeriesDataset mDataset = null;
			private boolean mResult = false;

			@Override
			public boolean fillView() {
				if (null != mContext) {
					try {
						loadData();
						String title = getAccessor().getTableTitle(
								mContext.getRecord());
						mRenderer = RendererFactory.buildLineRenderer(
								mContext.getContext(), Color.BLUE);

						addXTitles(mRenderer, 20);

						mDataset = new XYMultipleSeriesDataset();
						mDataset.addSeries(getDataset(title));

						mResult = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return mResult;
			}

			@Override
			public View onFinish() {
				View view = null;
				if (mResult) {
					view = ChartFactory.getLineChartView(mContext.getContext(),
							mDataset, mRenderer);
				}
				return view;
			}
		});
	}

	public DatabaseAccessor getAccessor() {
		if (null != mContext) {
			return Predictor.getPredictor().getAccessor(mContext.getContext());
		}
		return null;
	}

	@Override
	public int[] getEntryIds() {
		return new int[] { R.string.record_detail_spinner_pie,
				R.string.record_detail_spinner_bar,
				R.string.record_detail_spinner_line };
	}

	private void loadData() {
		if (null != mContext && null == mData) {
			Total total = mContext.getTotal();
			Time query = new Time();
			query.setPid(total.getId());
			mData = getAccessor().R(query);
			Collections.sort(mData, new Comparator<Table>() {

				@Override
				public int compare(Table lhs, Table rhs) {
					return ((Time) lhs).getRegion() - ((Time) rhs).getRegion();
				}
			});
		}
	}

	private XYSeries getDataset(String title) {
		XYSeries dataset = new XYSeries(title);
		int i, j;
		Time time = null;
		for (i = 0, j = 0; i < REGION2TITLE.length; ++i) {
			if (j < mData.size()) {
				time = (Time) mData.get(j);
			}
			if (i == time.getRegion()) {
				dataset.add(i, time.getCount());
				++j;
			} else {
				dataset.add(i, 0);
			}
		}
		return dataset;
	}

	private void addXTitles(XYMultipleSeriesRenderer renderer, int skip) {
		String empty = "";
		renderer.clearXTextLabels();
		for (int i = 0; i < REGION2TITLE.length; ++i) {
			if (0 == i % skip) {
				renderer.addXTextLabel(i, REGION2TITLE[i]);
			} else {
				renderer.addXTextLabel(i, empty);
			}
		}
	}
}
