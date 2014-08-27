package gt.high5.chart.filler;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.table.DayOfMonth;
import gt.high5.database.table.Total;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

public class DayOfMonthDataFiller extends SimpleDataFiller<DayOfMonth> {

	private final static String[] SUFFIXES = { "st", "nd", "rd", "th" };
	private final static int DAYS_OF_A_MONTH = 31;

	@Override
	protected String getName(DayOfMonth record) {
		return getNameOFDay(record.getDay());
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
			DatabaseAccessor accessor = getAccessor();
			if (null != accessor) {
				mData = accessor.R(query);
			}
		}
	}

	@Override
	public int[] getEntryIds() {
		return new int[] { R.string.record_detail_spinner_pie,
				R.string.record_detail_spinner_bar,
				R.string.record_detail_spinner_line };
	}

	protected XYSeries getDataset(String title) {
		loadData();
		XYSeries dataset = new XYSeries(title);
		int i, j;
		DayOfMonth record = null;
		for (i = 0, j = 0; i < DAYS_OF_A_MONTH; ++i) {
			if (j < mData.size()) {
				record = (DayOfMonth) mData.get(j);
			}
			if (i == record.getDay()) {
				dataset.add(i, record.getCount());
				++j;
			} else {
				dataset.add(i, 0);
			}
		}
		return dataset;
	}

	protected void addXTitles(XYMultipleSeriesRenderer renderer, int skip) {
		String empty = "";
		renderer.clearXTextLabels();
		int last = -skip - 1;
		int i, j;
		DayOfMonth record = null;
		for (i = 0, j = 0; i < DAYS_OF_A_MONTH; ++i) {
			if (j < mData.size()) {
				record = (DayOfMonth) mData.get(j);
			}
			String title = empty;
			if (i == record.getDay()) {
				if (i - last > skip) {
					title = getName(record);
					last = i;
				}
				++j;
			}
			renderer.addXTextLabel(i, title);
		}
	}

	private String getNameOFDay(int day) {
		int digit = day % 10;// single digit
		int suffixIndex = digit >= SUFFIXES.length ? SUFFIXES.length - 1
				: digit - 1;// above 0
		suffixIndex = suffixIndex < 0 ? SUFFIXES.length - 1 : suffixIndex;// 0
		return day + SUFFIXES[suffixIndex];
	}
}
