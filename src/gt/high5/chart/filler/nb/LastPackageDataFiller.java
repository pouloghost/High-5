package gt.high5.chart.filler.nb;

import gt.high5.R;
import gt.high5.chart.core.SimpleDataFiller;
import gt.high5.database.table.nb.LastPackage;
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
	public int[] getEntryIds() {
		return new int[] { R.string.record_detail_spinner_pie };
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

}
