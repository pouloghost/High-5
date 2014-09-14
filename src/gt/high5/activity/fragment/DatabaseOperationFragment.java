package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.DBService;
import gt.high5.core.service.IgnoreSetService;
import gt.high5.core.service.PreferenceService;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

	Handler mHandler = new Handler();

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.db_operation_layout, container,
				false);
		((Button) root.findViewById(R.id.db_backup_db_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						backupDB(getActivity());
					}
				});
		((Button) root.findViewById(R.id.db_restore_db_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						restoreDB(getActivity());
					}
				});
		((Button) root.findViewById(R.id.db_clean_db_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						cleanDB(getActivity());
					}
				});
		((Button) root.findViewById(R.id.db_backup_pref_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						backupPref(getActivity());
					}

				});
		((Button) root.findViewById(R.id.db_restore_pref_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						restorePref(getActivity());
					}
				});
		return root;
	}

	private void backupDB(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {

					@Override
					public void run() {
						DBService.getDBService(context).backupDB(context,
								new DBService.Callbacks() {

									@Override
									public void success() {
									}

									@Override
									public void failed(int id) {
										mHandler.post(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(
														context,
														R.string.db_backup_failed,
														Toast.LENGTH_SHORT)
														.show();
											}
										});
									}
								});
					}
				} });
	}

	private void cleanDB(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {
					public void run() {
						DBService.getDBService(context).cleanDB(context,
								new DBService.Callbacks() {

									@Override
									public void success() {
										IgnoreSetService.getIgnoreSetService(
												context).initDefault();
										PackageProvider.resetProvider(context);
									}

									@Override
									public void failed(int id) {
										mHandler.post(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(
														context,
														R.string.db_clean_failed,
														Toast.LENGTH_SHORT)
														.show();
											}
										});
									}
								});
					}
				} });
	}

	private void restoreDB(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {

					@Override
					public void run() {
						DBService.getDBService(context).backupDB(context,
								new DBService.Callbacks() {

									@Override
									public void success() {
									}

									@Override
									public void failed(int id) {
										mHandler.post(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(
														context,
														R.string.db_restore_failed,
														Toast.LENGTH_SHORT)
														.show();
											}
										});
									}
								});
					}
				} });
	}

	private void backupPref(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {
					@Override
					public void run() {
						try {
							PreferenceService.getPreferenceReadService(context)
									.backup(context);
						} catch (Exception e) {
							e.printStackTrace();
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(context,
											R.string.db_backup_failed,
											Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				} });
	}

	private void restorePref(final Context context) {
		new AsyncTaskWithProgress(context)
				.execute(new Runnable[] { new Runnable() {
					@Override
					public void run() {
						try {
							PreferenceService.getPreferenceReadService(context)
									.restore(context);
						} catch (Exception e) {
							e.printStackTrace();
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(context,
											R.string.db_restore_failed,
											Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				} });
	}
}
