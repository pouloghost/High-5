package gt.high5.chart.core;

import gt.high5.R;
import gt.high5.core.predictor.Predictor;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;
import android.view.View;

public abstract class SimpleDataFiller<T> extends DataFiller {
	// protected DatabaseAccessor mAccessor = null;
	protected ArrayList<Table> mData = null;

	protected NumberFormat mTitleFormat = new NumberFormat() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		@Override
		public StringBuffer format(double value, StringBuffer buffer,
				FieldPosition field) {
			String name = getName((T) mData.get((int) value));
			return new StringBuffer(name);
		}

		@Override
		public StringBuffer format(long value, StringBuffer buffer,
				FieldPosition field) {
			throw new UnsupportedOperationException("Not yet implemented.");
		}

		@Override
		public Number parse(String string, ParsePosition position) {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	};

	@Override
	protected void addFillers() {
		for (int id : getEntryIds()) {
			switch (id) {
			case R.string.record_detail_spinner_pie:
				// pie chart
				mFillers.add(new ViewFiller() {
					private DefaultRenderer mRenderer = null;
					private CategorySeries mCategorySeries = null;
					private boolean mResult = false;

					@SuppressWarnings("unchecked")
					@Override
					public boolean fillView() {
						if (null != mContext) {
							try {
								loadData();
								String title = getAccessor().getTableTitle(
										mContext.getRecord());
								try {
									mRenderer = RendererFactory
											.buildPieRenderer(
													mContext.getContext(),
													getColors(mData.size()));
								} catch (Exception e) {
									e.printStackTrace();
								}
								mCategorySeries = new CategorySeries(title);
								T record = null;
								for (Table table : mData) {
									record = (T) table;
									mCategorySeries.add(getName(record),
											getCount(record));
								}

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
							view = ChartFactory.getPieChartView(
									mContext.getContext(), mCategorySeries,
									mRenderer);
						}
						return view;
					}
				});
				break;
			case R.string.record_detail_spinner_bar:
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

								addXTitles(mRenderer, 30);

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
							view = ChartFactory.getBarChartView(
									mContext.getContext(), mDataset, mRenderer,
									Type.DEFAULT);
						}
						return view;
					}
				});
				break;
			case R.string.record_detail_spinner_line:
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

								addXTitles(mRenderer, 30);

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
							view = ChartFactory.getLineChartView(
									mContext.getContext(), mDataset, mRenderer);
						}
						return view;
					}
				});
				break;
			default:
				break;
			}

		}
	}

	public DatabaseAccessor getAccessor() {
		if (null != mContext) {
			return Predictor.getPredictor().getAccessor(mContext.getContext());
		}
		return null;
	}

	protected abstract String getName(T record);

	protected abstract int getCount(T record);

	protected void loadData() {
		if (null != mContext && null == mData) {
			Total total = mContext.getTotal();
			RecordTable query = null;
			try {
				query = mContext.getRecord().newInstance();
				query.setPid(total.getId());
				DatabaseAccessor accessor = getAccessor();
				if (null != accessor) {
					mData = accessor.R(query);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected XYSeries getDataset(String title) {
		XYSeries dataset = new XYSeries(title);
		T record = null;
		int i = 0;
		for (Table table : mData) {
			record = (T) table;
			dataset.add(i, getCount(record));
			++i;
		}
		return dataset;
	}

	@SuppressWarnings("unchecked")
	protected void addXTitles(XYMultipleSeriesRenderer renderer, int skip) {
		String empty = "";
		renderer.clearXTextLabels();
		T record = null;
		int i = 0;
		for (Table table : mData) {
			record = (T) table;
			if (0 == i % skip) {
				renderer.addXTextLabel(i, getName(record));
			} else {
				renderer.addXTextLabel(i, empty);
			}
			++i;
		}
	}

}
