package gt.high5.core.provider;

import gt.high5.core.service.IgnoreSetService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         interface for provide packages used during a period of time
 * 
 *         also the factory class for getting one
 * 
 *         this won't guarantee singleton
 */
@SuppressWarnings("unchecked")
public abstract class PackageProvider {

	protected static int MEMORY_SIZE = 10;
	protected ActivityManager mActivityManager = null;

	private static Class<? extends PackageProvider>[] priority = null;
	static {
		priority = new Class[1];
		// priority[0] = HackPackageProvider.class;
		priority[0] = OrderPackageProvider.class;
	}

	private static PackageProvider mInstance = null;

	/**
	 * provide a proper packageProvider based on the context condition
	 * 
	 * @param context
	 *            application context
	 * @return an instance of PackageProvider
	 */
	public static PackageProvider getPackageProvider(Context context) {
		if (null == mInstance) {
			synchronized (PackageProvider.class) {
				if (null == mInstance) {
					for (Class<? extends PackageProvider> clazz : priority) {
						try {
							mInstance = (PackageProvider) clazz
									.getDeclaredConstructor((Class[]) null)
									.newInstance();
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return mInstance;
	}

	public static PackageProvider resetProvider(Context context) {
		mInstance = null;
		return getPackageProvider(context);
	}

	protected PackageProvider() throws CannotCreateException {
	}

	public List<ActivityManager.RecentTaskInfo> getLaunchableRecent(
			Context context) {
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

		for (int i = recents.size(); i > 0; --i) {
			if (ignoredSet.contains(recents.get(i - 1).baseIntent
					.getComponent().getPackageName())) {
				recents.remove(i - 1);
			}
		}

		return recents;
	}

	public ActivityManager.RecentTaskInfo getRunningTask(Context context) {
		if (null == mActivityManager) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Service.ACTIVITY_SERVICE);
		}
		List<ActivityManager.RecentTaskInfo> recents = mActivityManager
				.getRecentTasks(1, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
		return recents.get(0);
	}

	/**
	 * get the packages used since last call on this method
	 * 
	 * @param context
	 *            application context
	 * @return packages
	 */
	public abstract Collection<LaunchInfo> getChangedPackages(Context context);

	public abstract List<String> getLastPackageOrder(Context context);

	public abstract List<String> getNoneCalculateZone(Context context, int len);

	public class CannotCreateException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
}
