package gt.high5.collector;

import gt.high5.core.provider.PackageProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class LogCollector {
	private static LogCollector instance = null;

	private BufferedWriter mWriter = null;

	private LogCollector() {
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())) {
			File root = Environment.getExternalStorageDirectory();
			File logFile = new File(root.getAbsolutePath(), "high5data.txt");
			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				mWriter = new BufferedWriter(new FileWriter(logFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static LogCollector getLogCollector() {
		if (null == instance) {
			synchronized (LogCollector.class) {
				if (null == instance) {
					instance = new LogCollector();
				}
			}
		}
		return instance;
	}

	public void write(Jsonable obj, Context context) {
		try {
			mWriter.append(obj.toJson());
			mWriter.append('\t');
			PackageProvider packageProvider = PackageProvider
					.getPackageProvider(context);
			mWriter.append(JsonUtils.stringListToJson(packageProvider
					.getNoneCalculateZone(context, 10)));
			mWriter.append('\n');
			mWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
