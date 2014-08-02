package gt.high5.core.provider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.ActivityManager;
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

	private ArrayList<LaunchInfo> mRecentInfos = null;
	private ArrayList<String> mOrderedPackages = null;

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
		ArrayList<LaunchInfo> infos = getRecentLaunchInfo(context);
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

		mRecentInfos = currentBackup;
		return result;
	}

	private ArrayList<LaunchInfo> getRecentLaunchInfo(Context context) {

		ArrayList<LaunchInfo> infos = new ArrayList<LaunchInfo>(MEMORY_SIZE);
		mOrderedPackages.clear();

		List<ActivityManager.RecentTaskInfo> recents = getLaunchableRecent(context);

		for (ActivityManager.RecentTaskInfo recent : recents) {
			try {
				LaunchInfo info = extractLaunchInfoFromTaskInfo(recent);
				infos.add(info);
				mOrderedPackages.add(info.getPackage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return infos;
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
