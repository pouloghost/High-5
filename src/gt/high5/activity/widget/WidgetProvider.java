package gt.high5.activity.widget;

import gt.high5.R;
import gt.high5.core.service.LogService;
import gt.high5.core.service.PreferenceReadService;
import gt.high5.core.service.RecordService;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * @author GT
 * 
 *         widget provider and receiving other broadcast sent by system
 */
@SuppressLint("NewApi")
public class WidgetProvider extends AppWidgetProvider {

	public static final String LAUNCH_PACKAGE = "gt.high5.launch.package";
	public static final String UPDATE_PACKAGE = "gt.high5.update.package";

	private static final int LAUNCH_REQ = 0;
	private static final String LAUNCH_ACT = "gt.high5.launch";
	// public static final int UPDATE_INTERVAL = 15 * 60 * 1000;
	// public static final int UPDATE_INTERVAL = 15 * 1000;

	private static final int RECORD_REQ = 1;
	private static final String RECORD_ACT = "gt.high5.record";

	// public static final int RECORD_INTERVAL = 60 * 1000;
	// public static final int RECORD_INTERVAL = UPDATE_INTERVAL / 15;

	@Override
	public void onEnabled(Context context) {

		super.onEnabled(context);
		// start up all recording service
		recordCurrentStatus(context);
	}

	@Override
	public void onDisabled(Context context) {

		super.onDisabled(context);

		LogService.d(WidgetProvider.class, "disable", context);

		// shut down all recording service
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getUpdateIntent(context, null));
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		LogService.d(WidgetProvider.class, "recieved " + intent.getAction(),
				context);

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
		startInterval(context,
				PreferenceReadService.getPreferenceReadService(context)
						.getUpdateInterval(),
				getUpdateIntent(context, appWidgetIds));

		LogService.d(WidgetProvider.class, "update "
				+ PreferenceReadService.getPreferenceReadService(context)
						.getUpdateInterval(), context);

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * restart update and record when process is killed
	 * 
	 * @param context
	 */
	public static void restartUpdateAndRecord(Context context) {
		startInterval(context,
				PreferenceReadService.getPreferenceReadService(context)
						.getUpdateInterval(), getUpdateIntent(context, null));
		startInterval(context,
				PreferenceReadService.getPreferenceReadService(context)
						.getRecordInterval(), getRecordIntent(context));
	}

	/**
	 * refresh widget right now regardless of alarm manager
	 * 
	 * @param context
	 */
	public static void forceRefresh(Context context) {
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getUpdateIntent(context, null));
		startInterval(context,
				PreferenceReadService.getPreferenceReadService(context)
						.getUpdateInterval(), getUpdateIntent(context, null));
	}

	private static void startInterval(Context context, int interval,
			PendingIntent intent) {
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.set(AlarmManager.RTC, System.currentTimeMillis() + interval,
						intent);
	}

	private static PendingIntent getUpdateIntent(Context context,
			int[] appWidgetIds) {
		if (null == appWidgetIds) {
			ComponentName provider = new ComponentName(context,
					WidgetProvider.class);
			appWidgetIds = AppWidgetManager.getInstance(context)
					.getAppWidgetIds(provider);
		}
		Intent updateIntent = new Intent(
				AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetIds);
		updateIntent.setData(Uri.parse("high5://widget/update/"));
		return PendingIntent.getBroadcast(context, LAUNCH_REQ, updateIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private static PendingIntent getRecordIntent(Context context) {
		Intent recordIntent = new Intent(RECORD_ACT);
		recordIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		recordIntent.setData(Uri.parse("high5://widget/record"));
		return PendingIntent.getBroadcast(context, RECORD_REQ, recordIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private void recordCurrentStatus(Context context) {
		try {
			RecordService.getRecordService(context).record(context);

			startInterval(context, PreferenceReadService
					.getPreferenceReadService(context).getRecordInterval(),
					getRecordIntent(context));

			LogService.d(WidgetProvider.class, "record "
					+ PreferenceReadService.getPreferenceReadService(context)
							.getRecordInterval(), context);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}
}
