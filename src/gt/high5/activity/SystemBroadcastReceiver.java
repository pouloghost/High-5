package gt.high5.activity;

import gt.high5.activity.widget.WidgetProvider;
import gt.high5.core.service.IgnoreSetService;
import gt.high5.core.service.LogService;
import gt.high5.core.service.RecordService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author ayi.zty
 * 
 *         listen to system broadcasts
 */
public class SystemBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_PACKAGE_REPLACED.equalsIgnoreCase(intent.getAction())
				|| Intent.ACTION_PACKAGE_RESTARTED.equalsIgnoreCase(intent
						.getAction())) {
			// task manager killed broadcast.
			LogService.d(SystemBroadcastReceiver.class,
					"killed in task manager", context.getApplicationContext());
			WidgetProvider.restartUpdateAndRecord(context);
		} else if (Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(intent
				.getAction())) {
			if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
				// the package exists no more,
				// remove all records
				String name = intent.getData().getSchemeSpecificPart();
				LogService.d(SystemBroadcastReceiver.class,
						"uninstalling package:" + name,
						context.getApplicationContext());
				try {
					RecordService.getRecordService(context).removeRecords(name);
				} catch (Exception e) {
					e.printStackTrace();
				}

				IgnoreSetService.getIgnoreSetService(context)
						.update(name, true);

				WidgetProvider.forceRefresh(context);
			}
		}
	}

}
