package gt.high5.core.widget;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.core.service.PreferenceReadService;
import gt.high5.core.service.ReadService;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.curioustechizen.xlog.Log;

/**
 * @author GT
 * 
 */
@SuppressLint("NewApi")
public class GridAdapterService extends RemoteViewsService {

	private ArrayList<String> apps = new ArrayList<String>();

	private static PackageManager mPackageManager = null;

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		if (MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "get factory");
		}
		return new GridViewFactory();
	}

	private class GridViewFactory implements RemoteViewsFactory {

		@Override
		public int getCount() {
			if (PreferenceReadService.getPreferenceReadService(
					getApplicationContext()).shouldLog(this.getClass())) {
				// Log.d(MainActivity.LOG_TAG, "data set size " + apps.size());
			}
			return apps.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public RemoteViews getLoadingView() {
			return new RemoteViews(GridAdapterService.this.getPackageName(),
					R.layout.widget_loading);
		}

		@Override
		public RemoteViews getViewAt(int position) {
			if (PreferenceReadService.getPreferenceReadService(
					getApplicationContext()).shouldLog(this.getClass())) {
				// Log.d(MainActivity.LOG_TAG, "view at " + position);
			}
			if (apps.size() < position) {
				return null;
			}
			Context context = GridAdapterService.this;
			if (null == mPackageManager) {
				mPackageManager = context.getPackageManager();
			}
			// retrieve application icon and name
			String packageName = apps.get(position);
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_item);
			ApplicationInfo info;
			try {
				info = mPackageManager.getApplicationInfo(packageName,
						PackageManager.GET_META_DATA);
				remoteViews.setImageViewBitmap(R.id.app_icon,
						((BitmapDrawable) mPackageManager
								.getApplicationIcon(packageName)).getBitmap());
				remoteViews.setTextViewText(R.id.app_name,
						mPackageManager.getApplicationLabel(info));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				remoteViews.setImageViewResource(R.id.app_icon,
						R.drawable.ic_launcher);
			}
			// launch broadcast
			Intent fillInIntent = new Intent();
			fillInIntent.putExtra(WidgetProvider.LAUNCH_PACKAGE, packageName);
			remoteViews.setOnClickFillInIntent(R.id.app_wrapper, fillInIntent);
			return remoteViews;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public void onCreate() {
			if (MainActivity.isDebugging()) {
				// Log.d(MainActivity.LOG_TAG, "create factory");
			}
		}

		@Override
		public void onDataSetChanged() {
			try {
				apps = ReadService.getReadService(getApplicationContext())
						.getHigh5(getApplicationContext(), apps);
				if (PreferenceReadService.getPreferenceReadService(
						getApplicationContext()).shouldLog(this.getClass())) {
					// Log.d(MainActivity.LOG_TAG, "data set changed");
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onDestroy() {
		}

	}
}
