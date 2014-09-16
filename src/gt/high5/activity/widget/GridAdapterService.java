package gt.high5.activity.widget;

import gt.high5.R;
import gt.high5.core.service.LogService;
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

/**
 * @author GT
 * 
 */
@SuppressLint("NewApi")
public class GridAdapterService extends RemoteViewsService {

	private ArrayList<String> mApps = null;

	private static PackageManager mPackageManager = null;

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {

		LogService.d(GridAdapterService.class, "get factory",
				getApplicationContext());

		return new GridViewFactory();
	}

	private class GridViewFactory implements RemoteViewsFactory {

		@Override
		public int getCount() {

			LogService.d(GridAdapterService.class,
					"data set size " + mApps.size(), getApplicationContext());

			return mApps.size();
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

			LogService.d(GridAdapterService.class, "view at " + position,
					getApplicationContext());

			if (mApps.size() < position) {
				return null;
			}
			Context context = GridAdapterService.this;
			if (null == mPackageManager) {
				mPackageManager = context.getPackageManager();
			}
			// retrieve application icon and name
			String packageName = mApps.get(position);
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
				return null;
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

			LogService.d(GridAdapterService.class, "create factory",
					getApplicationContext());
		}

		@Override
		public void onDataSetChanged() {
			try {
				mApps = ReadService.getReadService(getApplicationContext())
						.getHigh5();

				LogService.d(GridAdapterService.class, "data set changed",
						getApplicationContext());
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
