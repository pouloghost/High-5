package gt.high5.core.service;

import gt.high5.core.predictor.Predictor;
import gt.high5.core.provider.LaunchInfo;
import gt.high5.core.provider.PackageProvider;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.reducer.Reducer;
import gt.high5.database.table.Total;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.support.v4.util.ArrayMap;

/**
 * @author ayi.zty
 * 
 *         service for record current state and write to current predictor's
 *         database
 */
public class RecordService {
	private static final int TRIM_MAX_COUNT = 100;
	private static final int FEATURE_REDUCTION_COUNT = 1000;

	private Context mContext = null;
	private DatabaseAccessor mAccessor = null;

	// singleton
	private static RecordService mInstance = null;

	private RecordService(Context context) {
		mContext = context;
		mAccessor = Predictor.getPredictor().getAccessor(mContext);
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
		postRecordDBOperation();
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
			Class<? extends RecordTable>[] clazzes = Predictor.getPredictor()
					.getTables();
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

	private void postRecordDBOperation() {
		trimDB();
		featureReduction();
		PreferenceService.getPreferenceReadService(mContext)
				.increaseRecordCount();
	}

	private void featureReduction() {
		PreferenceService service = PreferenceService
				.getPreferenceReadService(mContext);
		int count = service.getRecordCount();
		if (count > FEATURE_REDUCTION_COUNT) {
			RecordTable queryTable = null;
			ArrayMap<Class<? extends RecordTable>, List<Table>> records = new ArrayMap<Class<? extends RecordTable>, List<Table>>();
			List<Class<? extends RecordTable>> filtered = new LinkedList<Class<? extends RecordTable>>();
			Collections.addAll(filtered, Predictor.getPredictor().getTables());
			// read all data
			for (Class<? extends RecordTable> clazz : filtered) {
				service.setShouldRead(clazz, true);
				try {
					queryTable = clazz.newInstance();
					List<Table> tableList = mAccessor.R(queryTable);
					if (null != tableList) {
						records.put(clazz, tableList);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// filter
			for (Reducer reducer : Reducer.getReducers()) {
				filtered.removeAll(reducer.shouldRead(records));
			}
			// set preference
			for (Class<? extends RecordTable> clazz : filtered) {
				service.setShouldRead(clazz, false);
			}
			service.resetRecordCount();
		}
	}

	private void trimDB() {
		PreferenceService service = PreferenceService
				.getPreferenceReadService(mContext);
		int count = service.getRecordCount();
		int diff = count % TRIM_MAX_COUNT;
		if (0 == diff) {
			String sql = RawRecord.D(diff);
			mAccessor.excute(sql);
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
			RecordContext recordContext = new RecordContext(context, null);

			// create if no total exists
			List<Table> list = mAccessor.R(total);
			if (null == list) {// create a new one for new package
				if (total.initDefault(recordContext, null)) {
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
				// record all
				RawRecord rawRecord = new RawRecord();
				// record context
				rawRecord.record(recordContext, count);
				mAccessor.C(rawRecord);
				// each type of record
				Class<? extends RecordTable>[] clazzes = Predictor
						.getPredictor().getTables();
				for (Class<? extends RecordTable> clazz : clazzes) {
					recordTable(context, recordContext, rawRecord, clazz);
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
