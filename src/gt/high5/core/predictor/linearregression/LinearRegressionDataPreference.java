package gt.high5.core.predictor.linearregression;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * theta storage utils
 * 
 * @author GT
 * 
 */
public class LinearRegressionDataPreference {
	private static final String PREF_NAME = "LinearRegressionTheta";
	private SharedPreferences mPreferences = null;
	private static LinearRegressionDataPreference instance = null;

	private LinearRegressionDataPreference(Context context) {
		mPreferences = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
	}

	public static LinearRegressionDataPreference getPreference(Context context) {
		if (null == instance) {
			synchronized (LinearRegressionDataPreference.class) {
				if (null == instance) {
					instance = new LinearRegressionDataPreference(context);
				}
			}
		}
		return instance;
	}

	public float getTheta(Class<?> key) {
		return mPreferences.getFloat(key.getSimpleName(), 0);
	}

	public void setTheta(Class<?> key, float value) {
		mPreferences.edit().putFloat(key.getSimpleName(), value).commit();
	}

	public void batchUpdate(Map<Class<?>, Float> data) {
		SharedPreferences.Editor editor = mPreferences.edit();
		for (Class<?> key : data.keySet()) {
			editor.putFloat(key.getSimpleName(), data.get(key));
		}
		editor.commit();
	}
}
