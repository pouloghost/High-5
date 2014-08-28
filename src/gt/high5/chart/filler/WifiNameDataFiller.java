package gt.high5.chart.filler;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.WifiName;

public class WifiNameDataFiller extends SimpleDataFiller<WifiName> {

	@Override
	protected String getName(WifiName record) {
		return record.getBssid();
	}

	@Override
	protected int getCount(WifiName record) {
		return record.getCount();
	}

	@Override
	public int[] getEntryIds() {
		return new int[] { R.string.record_detail_spinner_pie };
	}

}
