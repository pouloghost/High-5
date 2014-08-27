package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.IgnoreSetService;
import gt.high5.database.accessor.DatabaseAccessor;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class DatabaseOperationFragment extends Fragment {

	class AsyncTaskWithProgress extends AsyncTask<Runnable, Void, Void> {
		private ProgressDialog mDialog = null;

		public AsyncTaskWithProgress(Context context) {
			mDialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog.show();
			mDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(Runnable... runnables) {
			runnables[0].run();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mDialog.dismiss();
		}

	}

	/**
	 * db definition xml files, the R.xml.xx
	 */
	private static final int[] dbs = new int[] { R.xml.tables };

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.db_operation_layout, container,
				false);
		((Button) root.findViewById(R.id.db_backup_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						backup(getActivity());
					}
				});
		((Button) root.findViewById(R.id.db_restore_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						restore(getActivity());
					}
				});
		((Button) root.findViewById(R.id.db_clean_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						clean(getActivity());
					}
				});
		return root;
	}

	private void backup(final Context context) {
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

	private void clean(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {
					@Override
					public void run() {
						for (int id : dbs) {
							try {
								DatabaseAccessor.getAccessor(context, id)
										.clean(context);
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context,
										R.string.db_clean_failed,
										Toast.LENGTH_SHORT).show();
							}
						}
						IgnoreSetService.getIgnoreSetService(context)
								.initDefault();
						PackageProvider.resetProvider(context);
					}
				} });
	}

	private void restore(final Context context) {
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
}
