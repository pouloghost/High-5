package gt.high5;

import gt.high5.activity.MainActivity;
import gt.high5.core.service.IgnoreSetService;
import gt.high5.core.service.PreferenceReadService;
import gt.high5.database.accessor.FilterParser;
import gt.high5.database.filter.Filter;
import gt.high5.database.model.TableUtils;
import gt.high5.database.tables.Time;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.github.curioustechizen.xlog.Log;

public class High5Application extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		initPreferences();
		initDefaultIgnore();
		initLogFile();

	}

	private void initLogFile() {
		// init log file
		android.util.Log.d(MainActivity.LOG_TAG, "create application");
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())) {
			File root = Environment.getExternalStorageDirectory();
			android.util.Log.d(MainActivity.LOG_TAG,
					"root " + root.getAbsolutePath());
			File logFile = new File(root.getAbsolutePath(), "high5log.txt");
			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Log.init(this, true, logFile);
				android.util.Log.d(MainActivity.LOG_TAG,
						"inited " + logFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			android.util.Log.d(MainActivity.LOG_TAG,
					"state " + Environment.getExternalStorageState());
		}
	}

	private void initPreferences() {
		// init static preferences
		Time.setRegionLength(PreferenceReadService.getPreferenceReadService(
				getApplicationContext()).getRegionLength());
		TableUtils.setDebugging(PreferenceReadService.getPreferenceReadService(
				getApplicationContext()).shouldLog(TableUtils.class));
	}

	private void initDefaultIgnore() {
		// init default ignore list
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String key = "ignore_initialed";
		if (!prefs.getBoolean(key, false)) {
			try {
				FilterParser parser = new FilterParser(getResources().getXml(
						R.xml.filters));
				ArrayList<Filter> filters = parser.getFilters();

				List<ApplicationInfo> infos = getPackageManager()
						.getInstalledApplications(PackageManager.GET_META_DATA);

				IgnoreSetService service = IgnoreSetService
						.getIgnoreSetService(getApplicationContext());

				for (ApplicationInfo info : infos) {
					boolean shouldIgnore = false;
					for (Filter filter : filters) {
						shouldIgnore = filter.shouldIgnore(info.packageName);
						if (shouldIgnore) {
							service.update(info.packageName, false);
							break;
						}
					}
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			prefs.edit().putBoolean(key, true).commit();
		}
	}
}
