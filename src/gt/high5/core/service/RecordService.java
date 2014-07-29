package gt.high5.core.service;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.accessor.TableParser;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.tables.Total;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources.NotFoundException;

import com.github.curioustechizen.xlog.Log;

/**
 * @author ayi.zty
 * 
 *         service for record current state
 */
public class RecordService {

	private boolean isDebugging = false;

	private ActivityManager mActivityManager = null;
	private DatabaseAccessor mAccessor = null;
	// singleton
	private static RecordService instance = null;

	public static RecordService getRecordService(Context context)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NotFoundException, XmlPullParserException,
			IOException {
		if (null == instance) {
			instance = new RecordService(context);
		}
		return instance;
	}

	private RecordService(Context context) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, NotFoundException,
			XmlPullParserException, IOException {
		TableParser parser = new TableParser(context.getResources().getXml(
				R.xml.tables));
		mAccessor = DatabaseAccessor.getAccessor(context, parser, R.xml.tables);
	}

	/**
	 * record current status
	 * 
	 * @param context
	 *            application context for accessing system state
	 */
	public void record(Context context) {
		String packageName = getCurrentPackageName(context);

		if (null != packageName) {
			if (true || (isDebugging || MainActivity.isDebugging())) {
				Log.d(MainActivity.LOG_TAG, "current package " + packageName);
			}
			// read total with current package name
			Total total = new Total();
			total.setName(packageName);
			List<Table> list = mAccessor.R(total);
			if (null == list) {// create a new one for new package
				total.setCount(0);
				mAccessor.C(total);
				list = mAccessor.R(total);
			}
			// record
			List<Class<? extends RecordTable>> clazzes = mAccessor.getTables();
			if (null != list) {
				total = (Total) list.get(0);
				if ((isDebugging || MainActivity.isDebugging())) {
					Log.d(MainActivity.LOG_TAG, "total " + total.getName());
				}
				// each type of record
				for (Class<? extends RecordTable> clazz : clazzes) {
					if (isDebugging || MainActivity.isDebugging()) {
						Log.d(MainActivity.LOG_TAG,
								"updating class " + clazz.getSimpleName());
					}
					try {
						// read an existing record with the current pid and
						// status
						RecordTable table = clazz.newInstance();
						table.setPid(total.getId());
						table.currentQueryStatus(context);
						list = mAccessor.R(table);

						if (null == list) {// non-existing condition for this
											// app, create one record
							if (isDebugging || MainActivity.isDebugging()) {
								Log.d(MainActivity.LOG_TAG, "create new "
										+ table.getClass().getSimpleName());
							}
							table.initDefault(context);
							table.setPid(total.getId());
							mAccessor.C(table);
						} else {// existing condition just update
							if (isDebugging || MainActivity.isDebugging()) {
								Log.d(MainActivity.LOG_TAG, "increase old "
										+ table.getClass().getSimpleName());
							}
							table = (RecordTable) list.get(0);
							RecordTable select = table.clone();
							table.record(context);
							mAccessor.U(select, table);
						}
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private String getCurrentPackageName(Context context) {
		if (null == mAccessor) {
			return null;
		}
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "action record");
		}
		if (null == mActivityManager) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Service.ACTIVITY_SERVICE);
		}
		ActivityManager.RecentTaskInfo recent = mActivityManager
				.getRecentTasks(1, ActivityManager.RECENT_IGNORE_UNAVAILABLE)
				.get(0);
		String packageName = recent.baseIntent.getComponent().getPackageName();
		// read ignore list
		HashSet<String> ignoredSet = IgnoreSetService.getIgnoreSetService(
				context).getIgnoreSet();
		if (ignoredSet.contains(packageName)) {
			return null;
		} else {
			return packageName;
		}
	}
}
