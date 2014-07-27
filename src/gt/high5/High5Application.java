package gt.high5;

import java.io.File;
import java.io.IOException;

import com.github.curioustechizen.xlog.Log;

import android.app.Application;
import android.os.Environment;

public class High5Application extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
			File root = Environment.getExternalStorageDirectory();
			File logFile = new File(root.getAbsolutePath(), "high5log.txt");
			try {
				Log.init(this, true, logFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
