package gt.high5.activity.widget;

import gt.high5.R;
import gt.high5.core.service.LogService;
import gt.high5.core.service.PreferenceService;
import gt.high5.core.service.RecordService;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

	private static final int UPDATE_REQ = 2;

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

		LogService.d(WidgetProvider.class, "disable",
				context.getApplicationContext());
		stopUpdateAndRecord(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		LogService.d(WidgetProvider.class, "recieved " + intent.getAction(),
				context.getApplicationContext());

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
		startInterval(
				context,
				PreferenceService.getPreferenceReadService(
						context.getApplicationContext()).getUpdateInterval(),
				getUpdateIntent(context, appWidgetIds));

		LogService.d(
				WidgetProvider.class,
				"update "
						+ PreferenceService.getPreferenceReadService(
								context.getApplicationContext())
								.getUpdateInterval(), context
						.getApplicationContext());

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * restart update and record
	 * 
	 * @param context
	 */
	public static void restartUpdateAndRecord(Context context) {
		forceRecord(context);
		forceRefresh(context);
	}

	/**
	 * 
	 * 
	 * @param context
	 */
	public static void stopUpdateAndRecord(Context context) {
		// shut down all recording service
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getUpdateIntent(context, null));
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getRecordIntent(context));
	}

	/**
	 * refresh widget right now regardless of alarm manager
	 * 
	 * @param context
	 */
	public static void forceRefresh(Context context) {
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getUpdateIntent(context, null));
		try {
			getUpdateIntent(context, null).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	public static void forceRecord(Context context) {
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
				.cancel(getRecordIntent(context));
		try {
			getRecordIntent(context).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
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
		return PendingIntent.getBroadcast(context, UPDATE_REQ, updateIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private static PendingIntent getRecordIntent(Context context) {
		Intent recordIntent = new Intent(RECORD_ACT);
		recordIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		recordIntent.setData(Uri.parse("high5://widget/record"));
		return PendingIntent.getBroadcast(context, RECORD_REQ, recordIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private void recordCurrentStatus(final Context context) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					RecordService.getRecordService(context).record(context);
					LogService
							.d(WidgetProvider.class,
									"record "
											+ PreferenceService
													.getPreferenceReadService(
															context.getApplicationContext())
													.getRecordInterval(),
									context.getApplicationContext());

				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				startInterval(
						context,
						PreferenceService.getPreferenceReadService(
								context.getApplicationContext())
								.getRecordInterval(), getRecordIntent(context));
				super.onPostExecute(result);
			}

		}.execute();

	}
}
