package gt.high5.widget;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.accessor.TableParser;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.tables.Ignore;
import gt.high5.database.tables.Total;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.widget.RemoteViews;

import com.github.curioustechizen.xlog.Log;

/**
 * @author GT
 * 
 */
@SuppressLint("NewApi")
public class WidgetProvider extends AppWidgetProvider {

	private boolean isDebugging = false;

	public static final String LAUNCH_PACKAGE = "gt.high5.launch.package";
	public static final String UPDATE_PACKAGE = "gt.high5.update.package";

	private static final int LAUNCH_REQ = 0;
	private static final String LAUNCH_ACT = "gt.high5.launch";
	public static final int UPDATE_INTERVAL = 15 * 60 * 1000;
	// public static final int UPDATE_INTERVAL = 15 * 1000;

	private static final int RECORD_REQ = 1;
	private static final String RECORD_ACT = "gt.high5.record";
	// public static final int RECORD_INTERVAL = 60 * 1000;
	public static final int RECORD_INTERVAL = UPDATE_INTERVAL / 15;

	private static DatabaseAccessor mAccessor = null;

	private ActivityManager mActivityManager = null;

	@Override
	public void onEnabled(Context context) {

		super.onEnabled(context);
		// start up all recording service
		try {
			TableParser parser = new TableParser(context.getResources().getXml(
					R.xml.tables));
			mAccessor = DatabaseAccessor.getAccessor(context, parser,
					R.xml.tables);
			recordCurrentStatus(context);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisabled(Context context) {

		super.onDisabled(context);
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "disable");
		}
		// shut down all recording service
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getUpdateIntent(context, null));
		mAccessor = null;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (LAUNCH_ACT.equalsIgnoreCase(intent.getAction())) {
			String packageName = intent.getStringExtra(LAUNCH_PACKAGE);
			Intent i = context.getPackageManager().getLaunchIntentForPackage(
					packageName);
			context.startActivity(i);
		} else if (RECORD_ACT.equalsIgnoreCase(intent.getAction())) {
			recordCurrentStatus(context);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// init view
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "action update");
		}
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		// init intent template
		PendingIntent templateIntent = PendingIntent.getBroadcast(context,
				LAUNCH_REQ, new Intent(LAUNCH_ACT),
				PendingIntent.FLAG_CANCEL_CURRENT);
		views.setPendingIntentTemplate(R.id.launcher, templateIntent);
		// init adapter
		Intent adapterIntent = new Intent(context, GridAdapterService.class);
		adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetIds);
		views.setRemoteAdapter(R.id.launcher, adapterIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, views);
		// // update view
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
				R.id.launcher);
		// start update interval
		startInterval(context, UPDATE_INTERVAL,
				getUpdateIntent(context, appWidgetIds));
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private void startInterval(Context context, int interval,
			PendingIntent intent) {
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.set(AlarmManager.RTC, System.currentTimeMillis() + interval,
						intent);
	}

	private PendingIntent getUpdateIntent(Context context, int[] appWidgetIds) {
		Intent updateIntent = new Intent(
				AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetIds);
		updateIntent.setData(Uri.parse("high5://widget/update/"));
		return PendingIntent.getBroadcast(context, LAUNCH_REQ, updateIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private PendingIntent getRecordIntent(Context context) {
		Intent recordIntent = new Intent(RECORD_ACT);
		recordIntent.setData(Uri.parse("high5://widget/record"));
		return PendingIntent.getBroadcast(context, RECORD_REQ, recordIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private void recordCurrentStatus(Context context) {

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
		startInterval(context, RECORD_INTERVAL, getRecordIntent(context));
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
				.getRecentTasks(
						1,
						ActivityManager.RECENT_IGNORE_UNAVAILABLE
								| ActivityManager.RECENT_WITH_EXCLUDED).get(0);
		String packageName = recent.baseIntent.getComponent().getPackageName();
		// read ignore list
		Ignore ignoreQuery = new Ignore();
		ArrayList<Table> ignores = mAccessor.R(ignoreQuery);
		HashSet<String> ignoredSet = new HashSet<String>();
		if (null != ignores) {
			for (Table ignore : ignores) {
				ignoredSet.add(((Ignore) ignore).getName());
			}
		}
		if (ignoredSet.contains(packageName)) {
			return null;
		} else {
			return packageName;
		}
	}
}
