package gt.high5.chart.filler;

import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.LastPackage;
import gt.high5.database.table.Total;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class LastPackageDataFiller extends SimpleDataFiller<LastPackage> {

	private PackageManager mPackageManager = null;

	private PackageManager getPackageManager() {
		if (null != mContext && null == mPackageManager) {
			mPackageManager = mContext.getContext().getPackageManager();
		}
		return mPackageManager;
	}

	@Override
	protected String getName(LastPackage record) {
		String name = "";
		try {
			ApplicationInfo info = getPackageManager().getApplicationInfo(
					record.getLastPackage(), PackageManager.GET_META_DATA);
			name = mPackageManager.getApplicationLabel(info).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return name + ":" + getCount(record);
	}

	@Override
	protected int getCount(LastPackage record) {
		return record.getCount();
	}

	@Override
	protected void loadData() {
		if (null != mContext && null == mData) {
			Total total = mContext.getTotal();
			LastPackage query = new LastPackage();
			query.setPid(total.getId());
			getAccessor();
			if (null != mAccessor) {
				mData = mAccessor.R(query);
			}
		}
	}

}
