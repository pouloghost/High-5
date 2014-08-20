package gt.high5.chart.filler;

import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.Network;
import gt.high5.database.table.Total;

public class NetworkDataFiller extends SimpleDataFiller<Network> {

	@Override
	protected String getName(Network record) {
		return record.getConnection() + ":" + getCount(record);
	}

	@Override
	protected int getCount(Network record) {
		return record.getCount();
	}

	@Override
	protected void loadData() {
		if (null != mContext && null == mData) {
			Total total = mContext.getTotal();
			Network query = new Network();
			query.setPid(total.getId());
			getAccessor();
			if (null != mAccessor) {
				mData = mAccessor.R(query);
			}
		}
	}

}
