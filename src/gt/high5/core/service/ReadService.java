package gt.high5.core.service;

import gt.high5.R;
import gt.high5.activity.MainActivity;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.tables.Total;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.github.curioustechizen.xlog.Log;

/**
 * @author ayi.zty
 * 
 *         service for read records without specified type
 */
public class ReadService {

	private boolean isDebugging = true;
	// singleton
	private static ReadService instance = null;

	public static ReadService getReadService(Context context) {
		if (null == instance) {
			instance = new ReadService(context);
		}
		return instance;
	}

	private DatabaseAccessor mAccessor = null;

	private ReadService(Context context) {
		if (null == mAccessor) {
			if (isDebugging || MainActivity.isDebugging()) {
				// Log.d(MainActivity.LOG_TAG, "get a new accessor");
			}
			mAccessor = DatabaseAccessor.getAccessor(context, R.xml.tables);
		}
	}

	/**
	 * get highest 5 possible package
	 * 
	 * @param context
	 *            context to access system state
	 * @param last
	 *            last highest 5 package
	 * @return high 5 package
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public ArrayList<String> getHigh5(Context context, ArrayList<String> last)
			throws InstantiationException, IllegalAccessException {

		if (null != mAccessor) {
			last.clear();
			// read all packages
			Total queryTotal = new Total();
			ArrayList<Table> allTotals = mAccessor.R(queryTotal);
			// read all counts in total
			String column = "SUM(count)";
			Cursor cursor = mAccessor.query("SELECT " + column + " FROM "
					+ Total.class.getSimpleName());
			cursor.moveToFirst();
			int all = cursor.getInt(cursor.getColumnIndex(column));

			List<Class<? extends RecordTable>> tables = mAccessor.getTables();
			if (null != allTotals) {
				for (Table total : allTotals) {
					((Total) total).setPossibility(all, true);
					for (Class<? extends RecordTable> clazz : tables) {
						RecordTable queryTable = clazz.newInstance();
						queryTable.currentQueryStatus(context);
						ArrayList<Table> allTables = mAccessor.R(queryTable);
						if (null != allTables) {
							((Total) total).setPossibility(
									((RecordTable) total).getCount(), false);
						} else {
							// no existing record meaning user won't use this
							// app in current condition
							((Total) total).setPossibility(0, false);
						}
					}
				}

				Collections.sort(allTotals, Total.getComparator());

				if (isDebugging || MainActivity.isDebugging()) {
					StringBuilder sb = new StringBuilder();
					for (Table total : allTotals) {
						sb.append(((Total) total).getName());
						sb.append(":");
						sb.append(((Total) total).getPossibility());
						sb.append("\t");
					}

					Log.d(MainActivity.LOG_TAG, "after sort " + sb.toString());
				}

				HashSet<String> ignoredSet = IgnoreSetService
						.getIgnoreSetService(context).getIgnoreSet(mAccessor);

				int listSize = allTotals.size();
				int size = Math.min(5, listSize);
				for (int i = 0, j = 0; j < size && i < listSize; ++i) {
					String name = ((Total) allTotals.get(i)).getName();
					if (!ignoredSet.contains(name)) {
						last.add(name);
						++j;
					}
				}
			}
		} else {
			if (isDebugging || MainActivity.isDebugging()) {
				Log.d(MainActivity.LOG_TAG, "data accessor is null");
			}
		}
		return last;
	}
}
