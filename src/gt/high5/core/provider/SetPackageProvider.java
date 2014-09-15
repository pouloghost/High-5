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

	private ArrayList<String> mRecentPackage = null;

	public SetPackageProvider() throws CannotCreateException {
		super();
	}

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
			// record nothing
			packages.clear();
		}

		mRecentPackage = currentBackup;

		ArrayList<LaunchInfo> result = new ArrayList<LaunchInfo>(
				packages.size());
		for (String name : packages) {
			result.add(new LaunchInfo(name, 1));
		}
		return result;
	}

	@Override
	public List<String> getLastPackageOrder(Context context) {
		if (null == mRecentPackage) {
			mRecentPackage = getRecentPackages(context);
		}
		return mRecentPackage;
	}

	@Override
	public List<String> getNoneCalculateZone(Context context, int len) {
		return null;
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
}
