package gt.high5.chart.filler.nb;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.nb.WeekDay;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

public class WeekDayDataFiller extends SimpleDataFiller<WeekDay> {

	private final static String[] DAY2TITLE = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };

	@Override
	protected String getName(WeekDay record) {
		return DAY2TITLE[record.getDay()];
	}

	@Override
	protected int getCount(WeekDay record) {
		return record.getCount();
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
		WeekDay record = null;
		for (i = 0, j = 0; i < DAY2TITLE.length; ++i) {
			if (j < mData.size()) {
				record = (WeekDay) mData.get(j);
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
		WeekDay record = null;
		for (i = 0, j = 0; i < DAY2TITLE.length; ++i) {
			if (j < mData.size()) {
				record = (WeekDay) mData.get(j);
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

}
