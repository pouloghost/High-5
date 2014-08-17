package gt.high5.core.service;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * @author GT
 * 
 *         utils backing up db
 */
public class DBOperationService {

	/**
	 * db definition xml files, the R.xml.xx
	 */
	private static final int[] dbs = new int[] { R.xml.tables };

	public static void backup(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {

					@Override
					public void run() {
						for (int id : dbs) {
							try {
								DatabaseAccessor.getAccessor(context, id)
										.backup();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context,
										R.string.db_backup_failed,
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				} });
	}

	public static void restore(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {

					@Override
					public void run() {
						for (int id : dbs) {
							try {
								DatabaseAccessor.getAccessor(context, id)
										.restore();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context,
										R.string.db_restore_failed,
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				} });
	}

	public static void clean(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {

					@Override
					public void run() {
						for (int id : dbs) {
							try {
								DatabaseAccessor.getAccessor(context, id)
										.clean();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context,
										R.string.db_clean_failed,
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				} });
	}

	static class AsyncTaskWithProgress extends AsyncTask<Runnable, Void, Void> {
		private ProgressDialog mDialog = null;

		public AsyncTaskWithProgress(Context context) {
			mDialog = new ProgressDialog(context);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog.show();
		}

		@Override
		protected Void doInBackground(Runnable... runnables) {
			runnables[0].run();
			return null;
		}

	}
}
