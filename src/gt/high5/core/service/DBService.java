package gt.high5.core.service;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import android.content.Context;

/**
 * @author GT
 * 
 *         DB file operation wrapper
 * 
 */
public class DBService {

	private static DBService mInstance = null;

	public static DBService getDBService(Context context) {
		if (null == mInstance) {
			synchronized (DBService.class) {
				if (null == mInstance) {
					mInstance = new DBService();
				}
			}
		}
		return mInstance;
	}

	private DBService() {
	}

	public interface Callbacks {
		public void success();

		public void failed(int id);
	}

	/**
	 * db definition xml files, the R.xml.xx
	 */
	private static final int[] dbs = new int[] { R.xml.nb_tables,
			R.xml.cf_tables };

	public void backupDB(Context context, Callbacks callback) {
		for (int id : dbs) {
			try {
				DatabaseAccessor.getAccessor(context, id).backup();
			} catch (Exception e) {
				e.printStackTrace();
				if (null != callback) {
					callback.failed(id);
				}
			}
		}
		if (null != callback) {
			callback.success();
		}
	}

	public void cleanDB(Context context, Callbacks callback) {
		for (int id : dbs) {
			try {
				DatabaseAccessor.getAccessor(context, id).clean(context);
			} catch (Exception e) {
				e.printStackTrace();
				if (null != callback) {
					callback.failed(id);
				}
			}
		}
		if (null != callback) {
			callback.success();
		}
	}

	public void restoreDB(Context context, Callbacks callback) {
		for (int id : dbs) {
			try {
				DatabaseAccessor.getAccessor(context, id).restore();
			} catch (Exception e) {
				e.printStackTrace();
				if (null != callback) {
					callback.failed(id);
				}
			}
		}
		if (null != callback) {
			callback.success();
		}
	}

}
