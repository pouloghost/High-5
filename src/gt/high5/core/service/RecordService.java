package gt.high5.core.service;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.core.provider.LaunchInfo;
import gt.high5.core.provider.PackageProvider;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.accessor.TableParser;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.tables.Total;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

import com.github.curioustechizen.xlog.Log;

/**
 * @author ayi.zty
 * 
 *         service for record current state
 */
public class RecordService {

	private boolean isDebugging = false;

	private DatabaseAccessor mAccessor = null;
	private PackageProvider mPackageProvider = null;

	// singleton
	private static RecordService instance = null;

	public static RecordService getRecordService(Context context)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NotFoundException, XmlPullParserException,
			IOException {
		if (null == instance) {
			instance = new RecordService(context);
		}
		return instance;
	}

	private RecordService(Context context) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, NotFoundException,
			XmlPullParserException, IOException {
		TableParser parser = new TableParser(context.getResources().getXml(
				R.xml.tables));
		mAccessor = DatabaseAccessor.getAccessor(context, parser, R.xml.tables);
		mPackageProvider = PackageProvider.getPackageProvider(context);
	}

	/**
	 * record current status
	 * 
	 * @param context
	 *            application context for accessing system state
	 */
	public void record(Context context) {
		if (null == mAccessor || null == mPackageProvider) {
			return;
		}
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "action record");
		}
		Collection<LaunchInfo> packages = mPackageProvider
				.getChangedPackages(context);
		for (LaunchInfo info : packages) {
			recordPackage(context, info.getPackage(), info.getLaunchCount());
		}
	}

	/**
	 * save the record to db
	 * 
	 * @param context
	 *            application context for system status
	 * @param packageName
	 *            package
	 * @param count
	 *            launch count
	 */
	private void recordPackage(Context context, String packageName, int count) {
		if (null != packageName) {
			if ((isDebugging || MainActivity.isDebugging())) {
				Log.d(MainActivity.LOG_TAG, "current package " + packageName);
			}

			RecordContext recordContext = new RecordContext(context, this, null);
			// read total with current package name
			Total total = new Total();
			total.setName(packageName);
			List<Table> list = mAccessor.R(total);
			if (null == list) {// create a new one for new package
				if (total.initDefault(recordContext)) {
					mAccessor.C(total);
					list = mAccessor.R(total);
				}
			}
			// record
			if (null != list) {
				total = (Total) list.get(0);
				if ((isDebugging || MainActivity.isDebugging())) {
					Log.d(MainActivity.LOG_TAG, "total " + total.getName());
				}

				recordContext.setTotal(total);
				// each type of record
				List<Class<? extends RecordTable>> clazzes = mAccessor
						.getTables();
				for (Class<? extends RecordTable> clazz : clazzes) {
					if (isDebugging || MainActivity.isDebugging()) {
						Log.d(MainActivity.LOG_TAG,
								"updating class " + clazz.getSimpleName());
					}
					try {
						// read an existing record with the current pid and
						// status
						RecordTable table = clazz.newInstance();
						table.setPid(total.getId());
						if (table.currentQueryStatus(recordContext)) {
							list = mAccessor.R(table);

							if (null == list) {// non-existing condition for
												// this
												// app, create one record
								if (isDebugging || MainActivity.isDebugging()) {
									Log.d(MainActivity.LOG_TAG, "create new "
											+ table.getClass().getSimpleName());
								}
								if (table.initDefault(recordContext)) {
									table.setPid(total.getId());
									mAccessor.C(table);
								}
							} else {// existing condition just update
								if (isDebugging || MainActivity.isDebugging()) {
									Log.d(MainActivity.LOG_TAG, "increase old "
											+ table.getClass().getSimpleName());
								}
								table = (RecordTable) list.get(0);
								RecordTable select = table.clone();
								table.increaseCount(count);
								mAccessor.U(select, table);
							}
						}
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public PackageProvider getPackageProvider() {
		return mPackageProvider;
	}
}
