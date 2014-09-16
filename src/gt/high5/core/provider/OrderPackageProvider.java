package gt.high5.core.provider;

import gt.high5.core.service.LogService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;

/**
 * @author GT
 * 
 *         provider using reverse order of apps to find changed apps
 * 
 */
public class OrderPackageProvider extends PackageProvider {

	private ArrayList<String> mRecentPackage = null;
	private List<String> mLastChanged = null;

	public OrderPackageProvider() throws CannotCreateException {
		super();
	}

	@Override
	public Collection<LaunchInfo> getChangedPackages(Context context) {
		// get recent package
		ArrayList<String> packages = getRecentPackages(context);

		// backup for save to mRecentMemory
		StringBuilder log = new StringBuilder("current:\t");
		for (String name : packages) {
			log.append(name).append(";\t");
		}
		if (null != mRecentPackage) {// normal
			// the packages are in the same order if nothing happens
			// any inverse number means some actions are done to the app
			// package name 2 index mapping
			int i = getOldestChangeIndex(context, packages);
			mLastChanged = packages.subList(0, i);
		} else {// first time
			// record nothing
			mLastChanged = new ArrayList<String>();
		}

		mRecentPackage = packages;

		ArrayList<LaunchInfo> result = new ArrayList<LaunchInfo>(mLastChanged.size());
		log.append("\nleft:\t");
		for (String name : mLastChanged) {
			log.append(name).append(";\t");
			result.add(new LaunchInfo(name, 1));
		}
		LogService.d(PackageProvider.class, log.toString(), context);
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
		List<String> last = getLastPackageOrder(context);
		if (last.size() > len) {
			last = last.subList(0, len);
		}
		return last;
	}

	@Override
	public List<String> getLastChangedPackage() {
		return mLastChanged;
	}

	/**
	 * get the index of oldest change in recent task list
	 * 
	 * @param context
	 * @param packages
	 * @return
	 */
	private int getOldestChangeIndex(Context context, ArrayList<String> packages) {
		HashMap<String, Integer> indexOfPackage = new HashMap<String, Integer>();
		for (int i = 0; i < mRecentPackage.size(); ++i) {
			indexOfPackage.put(mRecentPackage.get(i), i);
		}
		// from the last app known
		int last = MEMORY_SIZE + 1;
		int i = packages.size() - 1;
		for (; i >= 0; --i) {
			Integer current = indexOfPackage.get(packages.get(i));
			if (null == current) {// non-existing app, newly added
				break;
			}
			if (current <= last) {// no inverse
				last = current;
			} else {// inverse
				break;
			}
		}
		// all right to the first one
		if (i == -1) {
			i = 0;
			RecentTaskInfo info = getRunningTask(context);
			if (null != info
					&& packages.size() > 0
					&& info.baseIntent.getComponent().getPackageName()
							.equals(packages.get(0))) {
				// the newest recent is running
				i = 1;
			}
		}
		return i;
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
