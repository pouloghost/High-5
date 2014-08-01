package gt.high5.core.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         using complement (set theory) to identify new package
 */
public class SetPackageProvider extends PackageProvider {

	public SetPackageProvider() throws CannotCreateException {
		super();
	}

	private ArrayList<String> mRecentPackage = null;

	@Override
	public Collection<LaunchInfo> getChangedPackages(Context context) {
		// get recent package
		ArrayList<String> packages = getRecentPackages(context);

		// backup for save to mRecentMemory
		@SuppressWarnings("unchecked")
		ArrayList<String> currentBackup = (ArrayList<String>) packages.clone();

		if (null != mRecentPackage) {// normal
			// the relative complement of mRecentMemory in memory
			packages.removeAll(mRecentPackage);

		} else {// first time
			// only record first one
			if (0 < packages.size()) {
				String newset = packages.get(0);
				packages.clear();
				packages.add(newset);
			}
		}

		mRecentPackage = currentBackup;

		ArrayList<LaunchInfo> result = new ArrayList<LaunchInfo>(
				packages.size());
		for (String name : packages) {
			result.add(new LaunchInfo(name, 1));
		}
		return result;
	}

	/**
	 * get recent packages up to MEMORY_SIZE
	 * 
	 * @param context
	 *            application context
	 * @param packages
	 *            the set to hold recent packages
	 * @return the most recent package
	 */
	private ArrayList<String> getRecentPackages(Context context) {

		ArrayList<String> packages = new ArrayList<String>(MEMORY_SIZE);

		List<ActivityManager.RecentTaskInfo> recents = getLaunchableRecent(context);

		for (ActivityManager.RecentTaskInfo recent : recents) {
			packages.add(recent.baseIntent.getComponent().getPackageName());
		}
		return packages;
	}

	@Override
	public List<String> getLastPackageOrder(Context context) {
		return mRecentPackage;
	}
}
