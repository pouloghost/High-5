package gt.high5.chart.filler.nb;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.nb.AbstractVolumn;

public class VolumnDataFiller extends SimpleDataFiller<AbstractVolumn> {

	private static int LENGTH = 10;

	@Override
	protected String getName(AbstractVolumn record) {
		return record.getPercent() + "0%";
	}

	@Override
	protected int getCount(AbstractVolumn record) {
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
		AbstractVolumn record = null;
		for (i = 0, j = 0; i < LENGTH; ++i) {
			if (j < mData.size()) {
				record = (AbstractVolumn) mData.get(j);
			}
			if (i == record.getPercent()) {
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
		AbstractVolumn record = null;
		for (i = 0, j = 0; i < LENGTH; ++i) {
			if (j < mData.size()) {
				record = (AbstractVolumn) mData.get(j);
			}
			String title = empty;
			if (i == record.getPercent()) {
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
