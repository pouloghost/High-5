package gt.high5.chart.core;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.ui.Formatter;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;

public abstract class SimpleDataFiller<T> extends DataFiller {

	protected DatabaseAccessor mAccessor = null;
	protected ArrayList<Table> mData = null;

	private NumberFormat mTitleFormat = new NumberFormat() {
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
		// pie chart
		mFillers.put(CHART_TYPE.PIE, new ViewFiller() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean fillView() {
				if (null != mContext) {
					loadData();
					if (null != mData) {

						int colors = initSegmentFormatters(mData.size());
						// fill data
						PieChart pieChart = (PieChart) mContext.getView();
						pieChart.clear();
						T record = null;
						for (int i = 0; i < mData.size(); ++i) {
							record = (T) mData.get(i);
							pieChart.addSeries(new Segment(getName(record),
									getCount(record)),
									pieFormatters[i % colors]);
						}
						pieChart.getBorderPaint().setColor(Color.TRANSPARENT);
						pieChart.getBackgroundPaint().setColor(
								Color.TRANSPARENT);
						return true;
					}
				}
				return false;
			}
		});
		// bar chart
		mFillers.put(CHART_TYPE.BAR, new ViewFiller() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean fillView() {
				if (null != mContext) {
					BarFormatter formatter = new BarFormatter();
					formatter.configure(mContext.getContext(),
							R.xml.bar_formatter);
					if (fillXYPlot(formatter)) {
						for (Object renderer : ((XYPlot) mContext.getView())
								.getRendererList()) {
							if (renderer instanceof BarRenderer<?>) {
								((BarRenderer<BarFormatter>) renderer)
										.setBarWidth(10);
							}
						}
						return true;
					}
				}
				return false;
			}
		});
		// line chart
		mFillers.put(CHART_TYPE.LINE, new ViewFiller() {

			@Override
			public boolean fillView() {
				if (null != mContext) {
					// format
					LineAndPointFormatter formatter = new LineAndPointFormatter();
					formatter.configure(mContext.getContext(),
							R.xml.line_point_formatter);
					return fillXYPlot(formatter);
				}
				return false;
			}
		});
	}

	public DatabaseAccessor getAccessor() {
		if (null != mContext && null == mAccessor) {
			mAccessor = DatabaseAccessor.getAccessor(mContext.getContext(),
					R.xml.tables);
		}
		return mAccessor;
	}

	public void setAccessor(DatabaseAccessor mAccessor) {
		this.mAccessor = mAccessor;
	}

	protected abstract String getName(T record);

	protected abstract int getCount(T record);

	protected abstract void loadData();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean fillXYPlot(Formatter<XYPlot> formatter) {
		loadData();
		if (null == mData) {
			return false;
		}
		// fill data
		XYPlot xyPlot = (XYPlot) mContext.getView();
		xyPlot.clear();
		// init data
		Number[] numbers = new Number[mData.size()];
		for (int i = 0; i < mData.size(); ++i) {
			numbers[i] = getCount((T) mData.get(i));
		}

		xyPlot.setTicksPerRangeLabel(10);
		xyPlot.setTicksPerDomainLabel(1);
		xyPlot.setDomainValueFormat(mTitleFormat);
		XYSeries series = new SimpleXYSeries(Arrays.asList(numbers),
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, getAccessor()
						.getTableTitle(mContext.getRecord()));
		xyPlot.setOnTouchListener(new ZoomAndDragListener(series));
		xyPlot.addSeries(series, (XYSeriesFormatter) formatter);

		return true;
	}

}
