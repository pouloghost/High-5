package gt.high5.core.provider;

import gt.high5.core.service.IgnoreSetService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         using PkgUsageStats hidden api to get packages
 */
public class HackPackageProvider extends PackageProvider {

	private Object mUsageStatsService = null;
	private Method mGetPkgUsageStatsMethod = null;
	private Field mLaunchCountField = null;

	private HashMap<String, LaunchInfo> mRecentInfos = new HashMap<String, LaunchInfo>(
			MEMORY_SIZE);
	private ActivityManager mActivityManager = null;

	public HackPackageProvider() throws CannotCreateException {
		super();
		try {
			Class<?> ServiceManager = Class
					.forName("android.os.ServiceManager");
			Method getService = ServiceManager.getMethod("getService",
					java.lang.String.class);
			Object usageService = getService.invoke(null, "usagestats");
			Class<?> Stub = Class
					.forName("com.android.internal.app.IUsageStats$Stub");
			Method asInterface = Stub.getMethod("asInterface",
					android.os.IBinder.class);
			mUsageStatsService = asInterface.invoke(null, usageService);
			mGetPkgUsageStatsMethod = mUsageStatsService.getClass().getMethod(
					"getPkgUsageStats", ComponentName.class);
			Class<?> PkgUsageStats = Class
					.forName("com.android.internal.os.PkgUsageStats");
			mLaunchCountField = PkgUsageStats.getDeclaredField("launchCount");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CannotCreateException();
		}
	}

	@Override
	public Collection<LaunchInfo> getChangedPackages(Context context) {
		// recent packages
		HashMap<String, LaunchInfo> infos = new HashMap<String, LaunchInfo>();
		LaunchInfo newest = getRecentLaunchInfo(context, infos);
		// backup
		@SuppressWarnings("unchecked")
		HashMap<String, LaunchInfo> currentBackup = (HashMap<String, LaunchInfo>) infos
				.clone();
		// filter information
		ArrayList<LaunchInfo> result = new ArrayList<LaunchInfo>(infos.size());
		for (String name : infos.keySet()) {
			LaunchInfo info = null;
			if (mRecentInfos.containsKey(name)) {
				// app is in recent list last call
				LaunchInfo now = infos.get(name);
				LaunchInfo last = mRecentInfos.get(name);
				// launch count increased
				if (now.getLaunchCount() > last.getLaunchCount()) {
					info = new LaunchInfo(name, now.getLaunchCount()
							- last.getLaunchCount());
				}
			} else {
				// newly added package, regarded as launched once
				info = new LaunchInfo(name, 1);
			}
			if (null != info) {
				result.add(info);
			}
		}

		if (0 == result.size()) {
			// no change in set
			// this app is being used all the time
			result.add(newest);
		}

		mRecentInfos = currentBackup;
		return result;
	}

	private LaunchInfo getRecentLaunchInfo(Context context,
			HashMap<String, LaunchInfo> infos) {

		infos.clear();

		if (null == mActivityManager) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Service.ACTIVITY_SERVICE);
		}
		List<ActivityManager.RecentTaskInfo> recents = mActivityManager
				.getRecentTasks(MEMORY_SIZE,
						ActivityManager.RECENT_IGNORE_UNAVAILABLE);
		// ignore set
		HashSet<String> ignoredSet = IgnoreSetService.getIgnoreSetService(
				context).getIgnoreSet();

		LaunchInfo result = null;
		for (ActivityManager.RecentTaskInfo recent : recents) {
			String name = recent.baseIntent.getComponent().getPackageName();
			if (!ignoredSet.contains(name)) {
				try {
					LaunchInfo info = extractLaunchInfoFromTaskInfo(recent);
					infos.put(name, info);
					if (null == result) {
						result = info;
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	private LaunchInfo extractLaunchInfoFromTaskInfo(
			ActivityManager.RecentTaskInfo taskInfo)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		ComponentName componentName = taskInfo.baseIntent.getComponent();
		Object pkgStats = mGetPkgUsageStatsMethod.invoke(mUsageStatsService,
				componentName);
		int count = mLaunchCountField.getInt(pkgStats);
		return new LaunchInfo(componentName.getPackageName(), count);
	}
}
