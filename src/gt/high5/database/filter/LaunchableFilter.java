package gt.high5.database.filter;

import android.content.pm.PackageManager;

public class LaunchableFilter implements Filter {

	private static PackageManager mPackageManager = null;

	@Override
	public boolean shouldIgnore(FilterContext context) {
		if (null == mPackageManager) {
			mPackageManager = context.getContext().getPackageManager();
		}
		return mPackageManager
				.getLaunchIntentForPackage(context.getInfo().packageName) == null;
	}

}
