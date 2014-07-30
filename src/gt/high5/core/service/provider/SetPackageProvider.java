package gt.high5.core.service.provider;

import gt.high5.core.service.IgnoreSetService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
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

	private HashSet<String> mRecentPackage = new HashSet<String>(MEMORY_SIZE);
	private ActivityManager mActivityManager = null;

	@Override
	public Collection<LaunchInfo> getChangedPackages(Context context) {
		// get recent package
		HashSet<String> packages = new HashSet<String>();
		String newest = getRecentPackages(context, packages);
		// backup for save to mRecentMemory
		@SuppressWarnings("unchecked")
		HashSet<String> currentBackup = (HashSet<String>) packages.clone();
		// the relative complement of mRecentMemory in memory
		packages.removeAll(mRecentPackage);
		// no change in set
		// consider the user to be using the same app, this is not accurate yet
		// no other better method
		if (0 == packages.size()) {
			packages.add(newest);
		}

		mRecentPackage = currentBackup;

		ArrayList<LaunchInfo> result = new ArrayList<LaunchInfo>(packages.size());
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
	private String getRecentPackages(Context context, HashSet<String> packages) {

		packages.clear();
		
		if (null == mActivityManager) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Service.ACTIVITY_SERVICE);
		}
		List<ActivityManager.RecentTaskInfo> recents = mActivityManager
				.getRecentTasks(MEMORY_SIZE,
						ActivityManager.RECENT_IGNORE_UNAVAILABLE);

		// read ignore list
		HashSet<String> ignoredSet = IgnoreSetService.getIgnoreSetService(
				context).getIgnoreSet();
		for (ActivityManager.RecentTaskInfo recent : recents) {
			String name = recent.baseIntent.getComponent().getPackageName();
			if (!ignoredSet.contains(name)) {
				packages.add(name);
			}
		}

		return recents.get(0).baseIntent.getComponent().getPackageName();
	}
}
