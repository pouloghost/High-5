package gt.high5.activity;

import gt.high5.core.service.IgnoreSetService;
import gt.high5.core.service.LogService;
import gt.high5.core.service.RecordService;
import gt.high5.core.widget.WidgetProvider;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;

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
			LogService.d(SystemBroadcastReceiver.class, "killed in task manager",
					context);
			WidgetProvider.restartUpdateAndRecord(context);
		} else if (Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(intent
				.getAction())) {
			if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
				// the package exists no more,
				// remove all records
				String name = intent.getData().getSchemeSpecificPart();
				LogService.d(SystemBroadcastReceiver.class,
						"uninstalling package:" + name, context);
				try {
					RecordService.getRecordService(context).removeRecords(name,
							context);
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

				IgnoreSetService.getIgnoreSetService(context)
						.update(name, true);

				WidgetProvider.forceRefresh(context);
			}
		}
	}

}
