package gt.high5.chart.filler;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.chart.core.ZoomAndDragListener;
import gt.high5.database.table.DayOfMonth;
import gt.high5.database.table.Total;
import gt.high5.database.table.WeekDay;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;

import com.androidplot.ui.Formatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;

public class DayOfMonthDataFiller extends SimpleDataFiller<DayOfMonth> {

	private final static String[] SUFFIXES = { "st", "nd", "rd", "th" };
	private final static int DAYS_OF_A_MONTH = 31;

	@Override
	protected String getName(DayOfMonth record) {
		return getPrefixOFDay(record.getDay()) + ":" + getCount(record);
	}

	@Override
	protected int getCount(DayOfMonth record) {
		return record.getCount();
	}

	@Override
	protected void loadData() {
		if (null != mContext && null == mData) {
			Total total = mContext.getTotal();
			DayOfMonth query = new DayOfMonth();
			query.setPid(total.getId());
			getAccessor();
			if (null != mAccessor) {
				mData = mAccessor.R(query);
			}
		}
	}

	@Override
	public int[] getEntryIds() {
		return new int[] { R.string.record_detail_spinner_pie,
				R.string.record_detail_spinner_bar,
				R.string.record_detail_spinner_line };
	}

	@Override
	protected boolean fillXYPlot(Formatter<XYPlot> formatter) {
		loadData();
		if (null == mData) {
			return false;
		}
		// fill data
		XYPlot xyPlot = mContext.getXyPlot();
		xyPlot.clear();
		// init data
		Number[] numbers = new Number[DAYS_OF_A_MONTH];
		// i for all the labels
		// j for record in db
		int i, j;
		WeekDay weekDay = null;
		for (i = 0, j = 0; i < DAYS_OF_A_MONTH; ++i) {
			if (j < mData.size()) {
				weekDay = (WeekDay) mData.get(j);
			}
			if (i == weekDay.getDay()) {
				numbers[i] = weekDay.getCount();
				++j;
			} else {
				numbers[i] = 0;
			}
		}

		xyPlot.setTicksPerRangeLabel(10);
		xyPlot.setTicksPerDomainLabel(1);
		xyPlot.setDomainValueFormat(new NumberFormat() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Number parse(String arg0, ParsePosition arg1) {
				throw new UnsupportedOperationException("Not yet implemented.");
			}

			@Override
			public StringBuffer format(long arg0, StringBuffer arg1,
					FieldPosition arg2) {
				throw new UnsupportedOperationException("Not yet implemented.");
			}

			@Override
			public StringBuffer format(double value, StringBuffer buffer,
					FieldPosition field) {
				int index = (int) value;
				index = index < DAYS_OF_A_MONTH ? index : DAYS_OF_A_MONTH;
				return new StringBuffer(getPrefixOFDay(index));
			}
		});
		XYSeries series = new SimpleXYSeries(Arrays.asList(numbers),
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
				mAccessor.getTableTitle(mContext.getRecord()));
		xyPlot.setOnTouchListener(new ZoomAndDragListener(series));
		xyPlot.addSeries(series, (XYSeriesFormatter<?>) formatter);

		mContext.setView2Show(xyPlot);

		return true;
	}

	private String getPrefixOFDay(int day) {
		int digit = day % 10;// single digit
		int suffixIndex = digit >= SUFFIXES.length ? SUFFIXES.length - 1
				: digit - 1;// above 0
		suffixIndex = suffixIndex < 0 ? SUFFIXES.length - 1 : suffixIndex;// 0
		return day + SUFFIXES[suffixIndex];
	}
}
