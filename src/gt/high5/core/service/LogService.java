package gt.high5.core.service;

import android.content.Context;

public class LogService {

	public static Context context = null;

	public static final String LOG_TAG = "GT";

	public static void initContext(Context context) {
		LogService.context = context;
	}

	public static void d(Class<?> clazz, String msg, Context context) {
		PreferenceService preference = PreferenceService
				.getPreferenceReadService(context);
		if (preference.shouldLog(clazz)) {
			if (preference.shouldLogToFile()) {
				com.github.curioustechizen.xlog.Log.d(LOG_TAG,
						"in " + clazz.getSimpleName() + ":\t" + msg);
			} else {
				android.util.Log.d(LOG_TAG, "in " + clazz.getSimpleName()
						+ ":\t" + msg);
			}
		}
	}

	public static void d(Class<?> clazz, String msg) {
		d(clazz, msg, context);
	}

	public static void e(Class<?> clazz, String msg, Context context) {
		if (null == context) {
			context = LogService.context;
		}
		PreferenceService preference = PreferenceService
				.getPreferenceReadService(context);
		if (preference.shouldLog(clazz)) {
			if (preference.shouldLogToFile()) {
				com.github.curioustechizen.xlog.Log.e(LOG_TAG,
						"in " + clazz.getSimpleName() + ":\t" + msg);
			} else {
				android.util.Log.e(LOG_TAG, "in " + clazz.getSimpleName()
						+ ":\t" + msg);
			}
		}
	}

	public static void e(Class<?> clazz, String msg) {
		e(clazz, msg, context);
	}

	public static void i(Class<?> clazz, String msg, Context context) {
		if (null == context) {
			context = LogService.context;
		}
		PreferenceService preference = PreferenceService
				.getPreferenceReadService(context);
		if (preference.shouldLog(clazz)) {
			if (preference.shouldLogToFile()) {
				com.github.curioustechizen.xlog.Log.i(LOG_TAG,
						"in " + clazz.getSimpleName() + ":\t" + msg);
			} else {
				android.util.Log.i(LOG_TAG, "in " + clazz.getSimpleName()
						+ ":\t" + msg);
			}
		}
	}

	public static void i(Class<?> clazz, String msg) {
		i(clazz, msg, context);
	}

	public static void v(Class<?> clazz, String msg, Context context) {
		if (null == context) {
			context = LogService.context;
		}
		PreferenceService preference = PreferenceService
				.getPreferenceReadService(context);
		if (preference.shouldLog(clazz)) {
			if (preference.shouldLogToFile()) {
				com.github.curioustechizen.xlog.Log.v(LOG_TAG,
						"in " + clazz.getSimpleName() + ":\t" + msg);
			} else {
				android.util.Log.v(LOG_TAG, "in " + clazz.getSimpleName()
						+ ":\t" + msg);
			}
		}
	}

	public static void v(Class<?> clazz, String msg) {
		v(clazz, msg, context);
	}

	public static void w(Class<?> clazz, String msg, Context context) {
		if (null == context) {
			context = LogService.context;
		}
		PreferenceService preference = PreferenceService
				.getPreferenceReadService(context);
		if (preference.shouldLog(clazz)) {
			if (preference.shouldLogToFile()) {
				com.github.curioustechizen.xlog.Log.w(LOG_TAG,
						"in " + clazz.getSimpleName() + ":\t" + msg);
			} else {
				android.util.Log.w(LOG_TAG, "in " + clazz.getSimpleName()
						+ ":\t" + msg);
			}
		}
	}

	public static void w(Class<?> clazz, String msg) {
		w(clazz, msg, context);
	}
}
