package gt.high5.widget;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.tables.Total;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@SuppressLint("NewApi")
public class GridAdapterService extends RemoteViewsService {

	private boolean isDebugging = false;

	private static DatabaseAccessor mAccessor = null;

	private ArrayList<String> apps = new ArrayList<String>();
	private PackageManager mPackageManager = null;

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		// TODO Auto-generated method stub
		if (MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "get factory");
		}
		return new GridViewFactory();
	}

	private class GridViewFactory implements RemoteViewsFactory {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (isDebugging || MainActivity.isDebugging()) {
				Log.d(MainActivity.LOG_TAG, "data set size " + apps.size());
			}
			return apps.size();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public RemoteViews getLoadingView() {
			// TODO Auto-generated method stub
			return new RemoteViews(GridAdapterService.this.getPackageName(),
					R.layout.widget_loading);
		}

		@Override
		public RemoteViews getViewAt(int position) {
			// TODO Auto-generated method stub
			if (isDebugging || MainActivity.isDebugging()) {
				Log.d(MainActivity.LOG_TAG, "view at " + position);
			}
			if (apps.size() < position) {
				return null;
			}
			Context context = GridAdapterService.this;
			if (null == mPackageManager) {
				mPackageManager = context.getPackageManager();
			}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
				remoteViews.setImageViewResource(R.id.app_icon,
						R.drawable.ic_launcher);
			}
			Intent fillInIntent = new Intent();
			fillInIntent.putExtra(WidgetProvider.LAUNCH_PACKAGE, packageName);
			remoteViews.setOnClickFillInIntent(R.id.app_wrapper, fillInIntent);
			return remoteViews;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			if (MainActivity.isDebugging()) {
				Log.d(MainActivity.LOG_TAG, "create factory");
			}
		}

		@Override
		public void onDataSetChanged() {
			// TODO Auto-generated method stub
			try {
				apps = getHigh5();
				if (isDebugging || MainActivity.isDebugging()) {
					Log.d(MainActivity.LOG_TAG, "data set changed");
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub

		}

	}

	private ArrayList<String> getHigh5() throws InstantiationException,
			IllegalAccessException {
		// TODO Auto-generated method stub
		if (null == mAccessor) {
			if (isDebugging || MainActivity.isDebugging()) {
				Log.d(MainActivity.LOG_TAG, "get a new accessor");
			}
			mAccessor = DatabaseAccessor.getAccessor(getApplicationContext(),
					R.xml.tables);
		}
		if (null != mAccessor) {
			apps.clear();
			Total queryTotal = new Total();
			ArrayList<RecordTable> allTotals = mAccessor.R(queryTotal);
			List<Class<? extends RecordTable>> tables = mAccessor.getTables();
			if (null != allTotals) {
				for (RecordTable total : allTotals) {
					for (Class<? extends RecordTable> clazz : tables) {
						RecordTable queryTable = clazz.newInstance();
						queryTable.initDefault(this);
						ArrayList<RecordTable> allTables = mAccessor.R(queryTable);
						if (null != allTables) {
							((Total) total).setPossibility(allTables.get(0)
									.getCount());
						}
					}
				}

				Collections.sort(allTotals, Total.getComparator());

				int size = Math.min(5, allTotals.size());
				for (int i = 0; i < size; ++i) {
					apps.add(((Total) allTotals.get(i)).getName());
				}
			}
		} else {
			if (isDebugging || MainActivity.isDebugging()) {
				Log.d(MainActivity.LOG_TAG, "data accessor is null");
			}
		}
		return apps;
	}
}
