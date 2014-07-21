package gt.high5.widget;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.accessor.TableParser;
import gt.high5.database.model.Table;
import gt.high5.database.model.Total;

import java.io.IOException;
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
import android.util.Log;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class WidgetProvider extends AppWidgetProvider {

	public static final String LAUNCH_PACKAGE = "gt.high5.launch.package";
	public static final String UPDATE_PACKAGE = "gt.high5.update.package";

	private static final int LAUNCH_REQ = 0;
	private static final String LAUNCH_ACT = "gt.high5.launch";
	private static final int UPDATE_INTERVAL = 15 * 60 * 1000;

	private static final int RECORD_REQ = 1;
	private static final String RECORD_ACT = "gt.high5.record";
	public static final int RECORD_INTERVAL = 1000;// 15 * 60 * 1000;

	private static DatabaseAccessor mAccessor = null;

	private ActivityManager mActivityManager = null;

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		// start up all recording service
		try {
			TableParser parser = new TableParser(context.getResources().getXml(
					R.xml.tables));
			mAccessor = DatabaseAccessor.getAccessor(context, parser,
					R.xml.tables);
			recordCurrentStatus(context);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		// shut down all recording service
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getUpdateIntent(context, null));
		mAccessor = null;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (LAUNCH_ACT.equalsIgnoreCase(intent.getAction())) {
			String packageName = intent.getStringExtra(LAUNCH_PACKAGE);
			Intent i = context.getPackageManager().getLaunchIntentForPackage(
					packageName);
			context.startActivity(i);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		// init view
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
		// update view
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
				R.id.launcher);
		appWidgetManager.updateAppWidget(appWidgetIds, views);
		Log.d(MainActivity.GT_TAG, "update");
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
		// read total with current package name
		Total total = new Total();
		total.setName(packageName);
		List<Table> list = mAccessor.R(total);
		if (null == list) {// create a new one for new package
			total.setCount(1);
			mAccessor.C(total);
			list = mAccessor.R(total);
		}
		List<Class<? extends Table>> clazzes = mAccessor.getTables();
		if (null != list) {
			total = (Total) list.get(0);
			// each type of record
			for (Class<? extends Table> clazz : clazzes) {
				try {
					Table table = clazz.newInstance();
					table.setPid(total.getId());
					list = mAccessor.R(table);
					if (null == list) {
						table.initDefault(context);
						table.setPid(total.getId());
						mAccessor.C(table);
						list = mAccessor.R(table);
					}
					if (null != list) {
						table = list.get(0);
						Table select = table.clone();
						table.record(context);
						mAccessor.U(select, table);
					}
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		startInterval(context, RECORD_INTERVAL, getRecordIntent(context));
	}
}
