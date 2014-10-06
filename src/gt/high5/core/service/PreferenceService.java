package gt.high5.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Environment;
import android.preference.PreferenceManager;

public class PreferenceService {

	private static String lOG_SUFFIX = "_Log";
	private static String READ_SUFFIX = "_Read";
	private static String RECORD_COUNT = "record_count";
	private static PreferenceService mInstance = null;

	@SuppressLint("SdCardPath")
	private static final String PREFERENCE_PATH = "/data/data/gt.high5/shared_prefs/";
	private static final String BACKUP_PATH = "high5";
	private static final String[] BACKUP_FILES = { "gt.high5_preferences.xml",
			"LinearRegressionTheta.xml" };

	private SharedPreferences mPreferences = null;
	private Context mContext = null;
	// preference listener exposed to outside
	private LinkedList<OnSharedPreferenceChangeListener> mPreferenceListeners = new LinkedList<OnSharedPreferenceChangeListener>();
	private OnSharedPreferenceChangeListener mListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences arg0,
				String arg1) {
			// update preference after it gets changed
			mPreferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			for (OnSharedPreferenceChangeListener listener : mPreferenceListeners) {
				listener.onSharedPreferenceChanged(arg0, arg1);
			}
		}
	};

	private PreferenceService(Context context) {
		mContext = context;
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public static PreferenceService getPreferenceReadService(Context context) {
		if (null == mInstance) {
			synchronized (PreferenceService.class) {
				if (null == mInstance) {
					mInstance = new PreferenceService(context);
				}
			}
		}
		return mInstance;
	}

	public int getUpdateInterval() {
		return Integer.parseInt(mPreferences.getString("update_interval",
				"600000"));
	}

	public int getRecordInterval() {
		return Integer.parseInt(mPreferences.getString("record_interval",
				"600000"));
	}

	public int getRegionLength() {
		return Integer.parseInt(mPreferences.getString("region_length", "15"));
	}

	public boolean shouldLog(Class<?> clazz) {
		String key = clazz.getSimpleName() + lOG_SUFFIX;
		return mPreferences.getBoolean(key, false);
	}

	public boolean shouldLogToFile() {
		return mPreferences.getBoolean("log_to_file", false);
	}

	// filter out unnecessary tables
	public boolean shouldRead(Class<?> clazz) {
		String key = clazz.getSimpleName() + READ_SUFFIX;
		return mPreferences.getBoolean(key, true);
	}

	public void setShouldRead(Class<?> clazz, boolean shouldRead) {
		String key = clazz.getSimpleName() + READ_SUFFIX;
		mPreferences.edit().putBoolean(key, shouldRead).commit();
	}

	public void backup() throws Exception {
		try {
			Exception exception = null;
			for (String name : BACKUP_FILES) {
				try {
					backupPreferenceFile(name);
				} catch (Exception e) {
					e.printStackTrace();
					exception = e;
				}
			}
			if (null != exception) {
				throw exception;
			}
		} finally {
			mPreferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
		}
	}

	public void restore() throws Exception {
		try {
			for (String name : BACKUP_FILES) {
				restorePreferenceFile(name);
			}
		} finally {
			mPreferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
		}
	}

	public int getRecordCount() {
		return mPreferences.getInt(RECORD_COUNT, 0);
	}

	public void increaseRecordCount() {
		int count = mPreferences.getInt(RECORD_COUNT, 0);
		mPreferences.edit().putInt(RECORD_COUNT, 1 + count).commit();
	}

	public void resetRecordCount() {
		mPreferences.edit().putInt(RECORD_COUNT, 0).commit();
	}

	// proxy for preferences
	public String getString(String key) {
		return mPreferences.getString(key, key);
	}

	// preference listeners
	public OnSharedPreferenceChangeListener getOnSharedPreferenceChangeListener() {
		return mListener;
	}

	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		mPreferenceListeners.add(listener);
	}

	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		mPreferenceListeners.remove(listener);
	}

	@SuppressWarnings("resource")
	private void backupPreferenceFile(String name) throws IOException,
			FileNotFoundException, Exception {
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())) {
			// source db
			String srcPath = PREFERENCE_PATH + name;
			File srcFile = new File(srcPath);
			// destination file
			File dstFolder = new File(
					Environment.getExternalStorageDirectory(), BACKUP_PATH);
			if (!dstFolder.exists()) {
				dstFolder.mkdir();
			}
			File dstFile = new File(dstFolder, name);
			if (!dstFile.exists()) {
				dstFile.createNewFile();
			}
			// copy
			FileChannel src = new FileInputStream(srcFile).getChannel();
			FileChannel dst = new FileOutputStream(dstFile).getChannel();

			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();
		} else {
			throw new Exception();
		}
	}

	@SuppressWarnings("resource")
	private void restorePreferenceFile(String name) throws Exception,
			FileNotFoundException, IOException {
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())) {
			// data/data db
			String dstPath = PREFERENCE_PATH + name;
			File dstFile = new File(dstPath);
			// backup file
			File srcFolder = new File(
					Environment.getExternalStorageDirectory(), BACKUP_PATH);
			if (!srcFolder.exists()) {
				throw new Exception();
			}
			File srcFile = new File(srcFolder, name);
			if (!srcFile.exists()) {
				throw new Exception();
			}
			// copy
			FileChannel src = new FileInputStream(srcFile).getChannel();
			FileChannel dst = new FileOutputStream(dstFile).getChannel();

			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();
		} else {
			throw new Exception();
		}
	}
}
