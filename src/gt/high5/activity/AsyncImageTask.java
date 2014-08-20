package gt.high5.activity;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * @author GT
 * 
 *         async task for loading app icon from packagemanager and set to
 *         imageview
 * 
 * @param Object
 *            [0] String the package name for retrieving app icon
 * @param Object
 *            [1] ImageView the image view for showing app icon
 */
public class AsyncImageTask extends AsyncTask<Object, Void, Drawable> {

	private ImageView imageView = null;
	private PackageManager mPackageManager = null;

	public AsyncImageTask(PackageManager packageManager) {
		mPackageManager = packageManager;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Drawable doInBackground(Object... arg0) {
		String packageName = (String) arg0[0];
		imageView = (ImageView) arg0[1];

		try {
			return mPackageManager.getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Drawable result) {
		super.onPostExecute(result);
		if (null != imageView && null != result) {
			imageView.setImageDrawable(result);
		}
	}
}