package gt.high5.core.service;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.tables.Ignore;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;

public class IgnoreSetService {

	private boolean isDebugging = true;
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
			if (isDebugging || MainActivity.isDebugging()) {
				// Log.d(MainActivity.LOG_TAG,
				// "get a new accessor in ignore set service");
			}
			mAccessor = DatabaseAccessor.getAccessor(context, R.xml.tables);
		}
	}

	public HashSet<String> getIgnoreSet() {
		if (null == mAccessor) {
			return null;
		}
		return getIgnoreSet(mAccessor);
	}

	public HashSet<String> getIgnoreSet(DatabaseAccessor accessor) {
		if (null == mIgnoreSet) {
			Ignore ignoreQuery = new Ignore();
			ArrayList<Table> ignores = accessor.R(ignoreQuery);
			mIgnoreSet.clear();
			if (null != ignores) {
				for (Table ignore : ignores) {
					mIgnoreSet.add(((Ignore) ignore).getName());
				}
			}
		}
		return mIgnoreSet;
	}

	public HashSet<String> update(String name, boolean ignored) {
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
