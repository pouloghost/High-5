package gt.high5.core.service;

import gt.high5.R;
import gt.high5.core.predictor.Predictor;
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

	private Context mContext = null;
	// singleton
	private static IgnoreSetService mInstance = null;

	private IgnoreSetService(Context context) {
		mContext = context;
	}

	public static IgnoreSetService getIgnoreSetService(Context context) {
		if (null == mInstance) {
			synchronized (IgnoreSetService.class) {
				if (null == mInstance) {
					mInstance = new IgnoreSetService(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * get ignore set, when not properly initialized may return null
	 * 
	 * @return ignore set
	 */
	public HashSet<String> getIgnoreSet() {
		DatabaseAccessor accessor = Predictor.getPredictor().getAccessor(
				mContext);
		if (null == accessor) {
			return null;
		}
		return getIgnoreSet(accessor);
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
		DatabaseAccessor accessor = Predictor.getPredictor().getAccessor(
				mContext);
		if (null == accessor) {
			new HashSet<String>();
		}
		if (null == mIgnoreSet) {
			getIgnoreSet(accessor);
		}
		Ignore ignoreQuery = new Ignore();
		ignoreQuery.setName(name);
		// change ignore status
		if (ignored) {// in database
			accessor.D(ignoreQuery);
			mIgnoreSet.remove(name);
		} else {
			accessor.C(ignoreQuery);
			mIgnoreSet.add(name);
		}

		return mIgnoreSet;
	}

	/**
	 * init default ignore set
	 * 
	 * @param context
	 *            application context
	 * @return whether default is initialized
	 */
	public boolean initDefault() {
		try {
			if (null != mIgnoreSet) {
				mIgnoreSet.clear();
			}
			FilterParser parser = new FilterParser(mContext.getResources()
					.getXml(R.xml.filters));
			ArrayList<Filter> filters = parser.getFilters();

			List<ApplicationInfo> infos = mContext.getPackageManager()
					.getInstalledApplications(PackageManager.GET_META_DATA);

			FilterContext filterContext = new FilterContext();
			filterContext.setContext(mContext.getApplicationContext());

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
