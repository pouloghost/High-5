package gt.high5.core.service;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.tables.Ignore;

import java.util.ArrayList;
import java.util.HashSet;

import com.github.curioustechizen.xlog.Log;

import android.content.Context;

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

	public static IgnoreSetService getIgnoreSetService(Context context) {
		if (null == instance) {
			instance = new IgnoreSetService(context);
		}
		return instance;
	}

	private IgnoreSetService(Context context) {
		if (null == mAccessor) {
			if (PreferenceReadService.getPreferenceReadService(context)
					.shouldLog(this.getClass())) {
				Log.d(MainActivity.LOG_TAG,
						"get a new accessor in ignore set service");
			}
			mAccessor = DatabaseAccessor.getAccessor(context, R.xml.tables);
		}
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
}
