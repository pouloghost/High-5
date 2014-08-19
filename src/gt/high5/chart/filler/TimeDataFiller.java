package gt.high5.chart.filler;

import gt.high5.R;
import gt.high5.chart.core.DataFiller;
import gt.high5.chart.core.FillContext;
import gt.high5.chart.core.ZoomAndDragListener;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.table.Time;
import gt.high5.database.table.Total;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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

public class TimeDataFiller extends DataFiller {

	private final static String[] REGION6TITLE = { "0:00-0:15", "0:15-0:30",
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
	private DatabaseAccessor mAccessor = null;
	private ArrayList<Table> mData = null;

	private NumberFormat mTitleFormat = new NumberFormat() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public StringBuffer format(double value, StringBuffer buffer,
				FieldPosition field) {
			return new StringBuffer(REGION6TITLE[(int) value]);
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

			@Override
			public boolean fillView() {
				if (null != mContext) {
					loadData();
					int colors = initSegmentFormatters(mData.size());
					// fill data
					PieChart pieChart = (PieChart) mContext.getView();
					pieChart.clear();
					Time time = null;
					for (int i = 0; i < mData.size(); ++i) {
						time = (Time) mData.get(i);
						pieChart.addSeries(
								new Segment(REGION6TITLE[time.getRegion()]
										+ ":" + time.getCount(), time
										.getCount()), pieFormatters[i % colors]);
					}
					pieChart.getBorderPaint().setColor(Color.TRANSPARENT);
					pieChart.getBackgroundPaint().setColor(Color.TRANSPARENT);
					return true;
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
					fillXYPlot(formatter);
					BarRenderer<BarFormatter> renderer = ((BarRenderer<BarFormatter>) ((XYPlot) mContext
							.getView()).getRendererList().get(0));
					renderer.setBarWidth(10);
					return true;
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
					fillXYPlot(formatter);
					return true;
				}
				return false;
			}
		});
	}

	public DatabaseAccessor getAccessor(FillContext context) {
		if (null == mAccessor) {
			mAccessor = DatabaseAccessor.getAccessor(context.getContext(),
					R.xml.tables);
		}
		return mAccessor;
	}

	public void setAccessor(DatabaseAccessor mAccessor) {
		this.mAccessor = mAccessor;
	}

	private void loadData() {
		if (null != mContext && null == mData) {
			Total total = mContext.getTotal();
			Time query = new Time();
			query.setPid(total.getId());
			mData = getAccessor(mContext).R(query);

			Collections.sort(mData, new Comparator<Table>() {

				@Override
				public int compare(Table lhs, Table rhs) {
					return ((Time) lhs).getRegion() - ((Time) rhs).getRegion();
				}
			});
		}
	}

	@SuppressWarnings("rawtypes")
	private void fillXYPlot(Formatter<XYPlot> formatter) {
		loadData();
		// fill data
		XYPlot xyPlot = (XYPlot) mContext.getView();
		xyPlot.clear();
		// init data
		Number[] numbers = new Number[REGION6TITLE.length];
		// i for all the labels
		// j for record in db
		int i, j;
		Time time = null;
		for (i = 0, j = 0; i < REGION6TITLE.length; ++i) {
			if (j < mData.size()) {
				time = (Time) mData.get(j);
			}
			if (i == time.getRegion()) {
				numbers[i] = time.getCount();
				++j;
			} else {
				numbers[i] = 0;
			}
		}

		xyPlot.setTicksPerRangeLabel(10);
		xyPlot.setTicksPerDomainLabel(1);
		xyPlot.setDomainValueFormat(mTitleFormat);
		XYSeries series = new SimpleXYSeries(Arrays.asList(numbers),
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
				mAccessor.getTableTitle(mContext.getRecord()));
		xyPlot.setOnTouchListener(new ZoomAndDragListener(series));
		xyPlot.addSeries(series, (XYSeriesFormatter) formatter);
	}
}
