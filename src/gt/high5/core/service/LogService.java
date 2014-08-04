package gt.high5.core.service;

import android.content.Context;

import com.github.curioustechizen.xlog.Log;

public class LogService {
	public static final String LOG_TAG = "GT";

	public static void d(Class<?> clazz, String msg, Context context) {
		if (PreferenceReadService.getPreferenceReadService(context).shouldLog(
				clazz)) {
			Log.d(LOG_TAG, "in " + clazz.getSimpleName() + ":\t" + msg);
		}
	}

	public static void e(Class<?> clazz, String msg, Context context) {
		if (PreferenceReadService.getPreferenceReadService(context).shouldLog(
				clazz)) {
			Log.e(LOG_TAG, "in " + clazz.getSimpleName() + ":\t" + msg);
		}
	}

	public static void i(Class<?> clazz, String msg, Context context) {
		if (PreferenceReadService.getPreferenceReadService(context).shouldLog(
				clazz)) {
			Log.i(LOG_TAG, "in " + clazz.getSimpleName() + ":\t" + msg);
		}
	}

	public static void v(Class<?> clazz, String msg, Context context) {
		if (PreferenceReadService.getPreferenceReadService(context).shouldLog(
				clazz)) {
			Log.v(LOG_TAG, "in " + clazz.getSimpleName() + ":\t" + msg);
		}
	}

	public static void w(Class<?> clazz, String msg, Context context) {
		if (PreferenceReadService.getPreferenceReadService(context).shouldLog(
				clazz)) {
			Log.w(LOG_TAG, "in " + clazz.getSimpleName() + ":\t" + msg);
		}
	}
}
