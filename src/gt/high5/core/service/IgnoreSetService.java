package gt.high5.core.service;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.accessor.FilterParser;
import gt.high5.database.filter.Filter;
import gt.high5.database.filter.FilterContext;
import gt.high5.database.model.Table;
import gt.high5.database.table.Ignore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * @author ayi.zty
 * 
 *         a service class to access and cache ignore set
 */
public class IgnoreSetService {

	// cached data
	private HashSet<String> mIgnoreSet = null;

	private DatabaseAccessor mAccessor = null;
	// singleton
	private static IgnoreSetService instance = null;

	private IgnoreSetService(Context context) {
		if (null == mAccessor) {
			mAccessor = DatabaseAccessor.getAccessor(context, R.xml.tables);
		}
	}

	public static IgnoreSetService getIgnoreSetService(Context context) {
		if (null == instance) {
			instance = new IgnoreSetService(context);
		}
		return instance;
	}

	/**
	 * get ignore set, when not properly initialized may return null
	 * 
	 * @return ignore set
	 */
	public HashSet<String> getIgnoreSet() {
		if (null == mAccessor) {
			return null;
		}
		return getIgnoreSet(mAccessor);
	}

	/**
	 * get ignore set always return something, empty set at least
	 * 
	 * @return ignore set
	 */
	public HashSet<String> getIgnoreSet(DatabaseAccessor accessor) {
		if (null == mIgnoreSet) {
			mIgnoreSet = new HashSet<String>();
			Ignore ignoreQuery = new Ignore();
			ArrayList<Table> ignores = accessor.R(ignoreQuery);
			if (null != ignores) {
				for (Table ignore : ignores) {
					mIgnoreSet.add(((Ignore) ignore).getName());
				}
			}
		}
		return mIgnoreSet;
	}

	/**
	 * change the status of package {@value name}, ignored or record
	 * 
	 * @param name
	 *            package name
	 * @param ignored
	 *            current state of this package
	 * @return updated ignore set
	 */
	public HashSet<String> update(String name, boolean ignored) {
		if (null == mIgnoreSet) {
			getIgnoreSet();
		}
		Ignore ignoreQuery = new Ignore();
		ignoreQuery.setName(name);
		// change ignore status
		if (ignored) {// in database
			mAccessor.D(ignoreQuery);
			mIgnoreSet.remove(name);
		} else {
			mAccessor.C(ignoreQuery);
			mIgnoreSet.add(name);
		}

		return mIgnoreSet;
	}

	/**
	 * init default ignore set
	 * @param context application context
	 * @return whether default is initialized
	 */
	public boolean initDefault(Context context) {
		try {
			FilterParser parser = new FilterParser(context.getResources()
					.getXml(R.xml.filters));
			ArrayList<Filter> filters = parser.getFilters();

			List<ApplicationInfo> infos = context.getPackageManager()
					.getInstalledApplications(PackageManager.GET_META_DATA);

			FilterContext filterContext = new FilterContext();
			filterContext.setContext(context.getApplicationContext());

			for (ApplicationInfo info : infos) {
				boolean shouldIgnore = false;
				for (Filter filter : filters) {
					filterContext.setInfo(info);
					shouldIgnore = filter.shouldIgnore(filterContext);
					if (shouldIgnore) {
						update(info.packageName, false);
						break;
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
