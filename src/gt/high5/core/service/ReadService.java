package gt.high5.core.service;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;

/**
 * @author ayi.zty
 * 
 *         service for read records without specified type
 */
public class ReadService {

	private static float MIN_POSSIBILITY = 1E-3f;
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

			RecordService service = null;
			try {
				service = RecordService.getRecordService(context);
				Class<? extends RecordTable>[] tables = mAccessor.getTables();
				if (null != allTotals) {
					for (Table total : allTotals) {
						updatePossibility(context, all, service, tables,
								(Total) total);
					}

					Collections.sort(allTotals, Total.getComparator());

					StringBuilder sortLog = new StringBuilder("after sort ");
					for (Table total : allTotals) {
						sortLog.append(((Total) total).getName());
						sortLog.append(":");
						sortLog.append(((Total) total).getPossibility());
						sortLog.append("\t");
					}
					LogService
							.d(ReadService.class, sortLog.toString(), context);

					HashSet<String> ignoredSet = IgnoreSetService
							.getIgnoreSetService(context).getIgnoreSet(
									mAccessor);

					int listSize = allTotals.size();
					int size = Math.min(5, listSize);
					for (int i = 0, j = 0; j < size && i < listSize; ++i) {
						if (MIN_POSSIBILITY > ((Total) allTotals.get(i))
								.getPossibility()) {// nearly impossible
							break;
						}
						String name = ((Total) allTotals.get(i)).getName();
						if (!ignoredSet.contains(name)) {
							last.add(name);
							++j;
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			LogService.d(ReadService.class, "data accessor is null", context);
		}
		return last;
	}

	private void updatePossibility(Context context, int all,
			RecordService service, Class<? extends RecordTable>[] tables,
			Total total) throws InstantiationException, IllegalAccessException {
		StringBuilder possibilityLog = new StringBuilder("Possible ");
		possibilityLog.append(total.getName());
		possibilityLog.append(" all:");
		possibilityLog.append(all);
		possibilityLog.append(".");

		total.setPossibility(all, true);
		for (Class<? extends RecordTable> clazz : tables) {
			RecordTable queryTable = clazz.newInstance();
			queryTable.currentQueryStatus(new RecordContext(context, service,
					total));
			ArrayList<Table> allTables = mAccessor.R(queryTable);
			if (null != allTables) {
				total.setPossibility(
						((RecordTable) allTables.get(0)).getCount(), false);
				possibilityLog.append(((RecordTable) allTables.get(0))
						.getClass().getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(((RecordTable) allTables.get(0))
						.getCount());
				possibilityLog.append(",");
			} else {
				// no existing record meaning user won't use this
				// app in current condition
				total.setPossibility(queryTable.getDefaultPossibility(context));
				possibilityLog.append(((RecordTable) queryTable).getClass()
						.getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(((RecordTable) queryTable)
						.getDefaultPossibility(context));
				possibilityLog.append(",");
			}
		}
		possibilityLog.append("possibility:");
		possibilityLog.append(total.getPossibility());
		LogService.d(ReadService.class, possibilityLog.toString(), context);
	}
}
