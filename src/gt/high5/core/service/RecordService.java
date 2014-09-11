package gt.high5.core.service;

import gt.high5.R;
import gt.high5.core.provider.LaunchInfo;
import gt.high5.core.provider.PackageProvider;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.table.Total;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;

/**
 * @author ayi.zty
 * 
 *         service for record current state
 */
public class RecordService {
	private static int XML_ID = R.xml.tables;
	private static int MAX_COUNT = 100;

	private Context mContext = null;
	private DatabaseAccessor mAccessor = null;
	private int mRawRecordCount = 0;

	// singleton
	private static RecordService mInstance = null;

	private RecordService(Context context) {
		mContext = context;
		mAccessor = DatabaseAccessor.getAccessor(mContext, XML_ID);
		Cursor cursor = mAccessor.query(RawRecord.RCount());
		if (cursor.moveToFirst()) {
			mRawRecordCount = cursor.getInt(0);
		}
		cursor.close();
	}

	public static RecordService getRecordService(Context context)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NotFoundException, XmlPullParserException,
			IOException {
		if (null == mInstance) {
			synchronized (RecordContext.class) {
				if (null == mInstance) {
					mInstance = new RecordService(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * record current status
	 * 
	 * @param context
	 *            application context for accessing system state
	 */
	public void record(Context context) {
		Collection<LaunchInfo> packages = PackageProvider.getPackageProvider(
				context).getChangedPackages(context);
		for (LaunchInfo info : packages) {
			recordPackage(context, info.getPackage(), info.getLaunchCount());
		}
		trimDB();
	}

	/**
	 * remove all data related with package
	 * 
	 * @param name
	 *            package name
	 */
	public void removeRecords(String name) {
		Total total = new Total();
		total.setName(name);
		List<Table> list = mAccessor.R(total);
		if (null != list) {
			total = (Total) list.get(0);
			mAccessor.D(total);
			Class<? extends RecordTable>[] clazzes = mAccessor.getTables();
			for (Class<? extends RecordTable> clazz : clazzes) {
				RecordTable table;
				try {
					table = clazz.newInstance();
					table.setPid(total.getId());
					mAccessor.D(table);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void trimDB() {
		int count = mRawRecordCount - MAX_COUNT;
		if (count > 0) {
			String sql = RawRecord.D(count);
			mAccessor.excute(sql);
			mRawRecordCount = MAX_COUNT;
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
			LogService.d(RecordService.class, "current package " + packageName,
					context.getApplicationContext());
			// read total with current package name
			Total total = new Total();
			total.setName(packageName);
			RecordContext recordContext = new RecordContext(context, total);
			// record all
			RawRecord rawRecord = new RawRecord();
			rawRecord.setCount(count);
			// create if no total exists
			List<Table> list = mAccessor.R(total);
			if (null == list) {// create a new one for new package
				if (total.initDefault(recordContext, rawRecord)) {
					mAccessor.C(total);
					list = mAccessor.R(total);
				}
			}
			// record
			if (null != list) {
				total = (Total) list.get(0);

				LogService.d(RecordService.class, "total " + total.getName(),
						context.getApplicationContext());

				recordContext.setTotal(total);
				// record context
				rawRecord.record(recordContext, count);
				mAccessor.C(rawRecord);
				++mRawRecordCount;
				// each type of record
				Class<? extends RecordTable>[] clazzes = mAccessor.getTables();
				for (Class<? extends RecordTable> clazz : clazzes) {
					if (Total.class != clazz) {
						recordTable(context, recordContext, rawRecord, clazz);
					}
				}
			}
		}
	}

	private void recordTable(Context context, RecordContext recordContext,
			RawRecord rawRecord, Class<? extends RecordTable> clazz) {
		List<Table> list;

		try {
			// read an existing record with the current pid and
			// status
			RecordTable table = clazz.newInstance();
			if (table.queryForRecord(recordContext, rawRecord)) {
				list = mAccessor.R(table);

				if (null == list) {// non-existing condition for
									// this
									// app, create one record
					LogService.d(RecordService.class, "create new "
							+ table.getClass().getSimpleName(),
							context.getApplicationContext());

					if (table.initDefault(recordContext, rawRecord)) {
						mAccessor.C(table);
					}
				} else {// existing condition just update

					LogService.d(RecordService.class, "increase old "
							+ table.getClass().getSimpleName(),
							context.getApplicationContext());

					table = (RecordTable) list.get(0);
					RecordTable select = (RecordTable) table.clone();
					table.increaseCount(rawRecord.getCount());
					mAccessor.U(select, table);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
