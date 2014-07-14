package gt.high5.widget;

import gt.high5.R;

import java.util.ArrayList;

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

	private ArrayList<String> apps = new ArrayList<String>();
	private PackageManager mPackageManager = null;

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("GT", "get factory");
		apps.add("com.baidu.input");
		apps.add("com.eg.android.AlipayGphone");
		apps.add("gt.high5");
		return new GridViewFactory();
	}

	private class GridViewFactory implements RemoteViewsFactory {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
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
			Log.d("GT", "view at " + position);
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
			Log.d("GT", "create factory");
		}

		@Override
		public void onDataSetChanged() {
			// TODO Auto-generated method stub
			apps = getHigh5();
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub

		}

	}

	private ArrayList<String> getHigh5() {
		// TODO Auto-generated method stub
		return apps;
	}
}
