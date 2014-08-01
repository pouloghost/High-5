package gt.high5.core.provider;

import gt.high5.core.service.IgnoreSetService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
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

	private ArrayList<LaunchInfo> mRecentInfos = new ArrayList<LaunchInfo>(
			MEMORY_SIZE);
	private ArrayList<String> mOrderedPackages = new ArrayList<String>(
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
		ArrayList<LaunchInfo> infos = new ArrayList<LaunchInfo>();
		LaunchInfo newest = getRecentLaunchInfo(context, infos);
		// backup
		@SuppressWarnings("unchecked")
		ArrayList<LaunchInfo> currentBackup = (ArrayList<LaunchInfo>) infos
				.clone();
		// filter information
		ArrayList<LaunchInfo> result = new ArrayList<LaunchInfo>(infos.size());
		if (null != mRecentInfos) {
			for (LaunchInfo now : infos) {
				LaunchInfo info = null;
				int index = mRecentInfos.indexOf(now);
				if (-1 != index) {
					// app is in recent list last call
					LaunchInfo last = mRecentInfos.get(index);
					// launch count increased
					if (now.getLaunchCount() > last.getLaunchCount()) {
						info = new LaunchInfo(now.getPackage(),
								now.getLaunchCount() - last.getLaunchCount());
					}
				} else {
					// newly added package, regarded as launched once
					info = new LaunchInfo(now.getPackage(), 1);
				}
				if (null != info) {
					result.add(info);
				}
			}
		} else {
			result.clear();
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
			ArrayList<LaunchInfo> infos) {

		infos.clear();
		mOrderedPackages.clear();

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
					infos.add(info);
					mOrderedPackages.add(info.getPackage());
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

	@Override
	public List<String> getLastPackageOrder(Context context) {
		return mOrderedPackages;
	}
}
