package gt.high5.chart.filler.nb;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.nb.Network;

public class NetworkDataFiller extends SimpleDataFiller<Network> {

	@Override
	public int[] getEntryIds() {
		return new int[] { R.string.record_detail_spinner_pie };
	}

	@Override
	protected String getName(Network record) {
		return record.getConnection();
	}

	@Override
	protected int getCount(Network record) {
		return record.getCount();
	}

}
