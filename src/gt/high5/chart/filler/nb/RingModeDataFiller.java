package gt.high5.chart.filler.nb;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.nb.RingMode;

public class RingModeDataFiller extends SimpleDataFiller<RingMode> {

	private static String[] MODE2NAME = { "Silent", "Vibrate", "Normal" };

	@Override
	protected String getName(RingMode record) {
		return MODE2NAME[record.getMode()];
	}

	@Override
	protected int getCount(RingMode record) {
		return record.getCount();
	}

	@Override
	public int[] getEntryIds() {
		return new int[] { R.string.record_detail_spinner_pie,
				R.string.record_detail_spinner_bar, };
	}

	protected XYSeries getDataset(String title) {
		loadData();
		XYSeries dataset = new XYSeries(title);
		int i, j;
		RingMode record = null;
		for (i = 0, j = 0; i < MODE2NAME.length; ++i) {
			if (j < mData.size()) {
				record = (RingMode) mData.get(j);
			}
			if (i == record.getMode()) {
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
		RingMode record = null;
		for (i = 0, j = 0; i < MODE2NAME.length; ++i) {
			if (j < mData.size()) {
				record = (RingMode) mData.get(j);
			}
			String title = empty;
			if (i == record.getMode()) {
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
