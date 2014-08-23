package gt.high5.chart.filler;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.chart.core.ZoomAndDragListener;
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

public class WeekDayDataFiller extends SimpleDataFiller<WeekDay> {

	private final static String[] DAY2TITLE = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };

	@Override
	protected String getName(WeekDay record) {
		return DAY2TITLE[record.getDay()] + ":" + getCount(record);
	}

	@Override
	protected int getCount(WeekDay record) {
		return record.getCount();
	}

	@Override
	protected void loadData() {
		if (null != mContext && null == mData) {
			Total total = mContext.getTotal();
			WeekDay query = new WeekDay();
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
		Number[] numbers = new Number[DAY2TITLE.length];
		// i for all the labels
		// j for record in db
		int i, j;
		WeekDay weekDay = null;
		for (i = 0, j = 0; i < DAY2TITLE.length; ++i) {
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
				index = index < DAY2TITLE.length ? index : DAY2TITLE.length;
				return new StringBuffer(DAY2TITLE[index]);
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
}
