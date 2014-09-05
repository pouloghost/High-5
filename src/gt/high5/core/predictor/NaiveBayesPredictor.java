package gt.high5.core.predictor;

import gt.high5.core.service.LogService;
import gt.high5.core.service.ReadService;
import gt.high5.core.service.RecordContext;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.DayOfMonth;
import gt.high5.database.table.LastPackage;
import gt.high5.database.table.Network;
import gt.high5.database.table.RingMode;
import gt.high5.database.table.RingVolumn;
import gt.high5.database.table.Time;
import gt.high5.database.table.Total;
import gt.high5.database.table.WeekDay;
import gt.high5.database.table.WifiName;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

/**
 * @author GT
 * 
 *         naive bayes
 */
public class NaiveBayesPredictor implements Predictor {

	private static final HashMap<Class<? extends RecordTable>, Integer> WEIGHTS = new HashMap<Class<? extends RecordTable>, Integer>();
	static {
		WEIGHTS.put(Total.class, 1);
		WEIGHTS.put(Time.class, 5);
		WEIGHTS.put(WifiName.class, 3);
		WEIGHTS.put(Network.class, 2);
		WEIGHTS.put(LastPackage.class, 3);
		WEIGHTS.put(WeekDay.class, 1);
		WEIGHTS.put(DayOfMonth.class, 1);
		WEIGHTS.put(RingVolumn.class, 2);
		WEIGHTS.put(RingMode.class, 2);
	}

	@Override
	public ArrayList<Table> predictPossibility(PredictContext context) {
		DatabaseAccessor accessor = context.getAccessor();
		// read all packages
		Total queryTotal = new Total();
		ArrayList<Table> allTotals = accessor.R(queryTotal);
		// read all counts in total
		String column = "SUM(count)";
		Cursor cursor = accessor.query("SELECT " + column + " FROM "
				+ Total.class.getSimpleName());
		cursor.moveToFirst();
		int all = cursor.getInt(cursor.getColumnIndex(column));

		try {

			if (null != allTotals) {
				for (Table total : allTotals) {
					updatePossibility(context, all, (Total) total);
				}
			}
		} catch (Exception e) {
			allTotals = null;
			e.printStackTrace();
		}
		return allTotals;
	}

	private void updatePossibility(PredictContext context, int all, Total total)
			throws InstantiationException, IllegalAccessException {
		StringBuilder possibilityLog = new StringBuilder("Possible ");
		possibilityLog.append(total.getName());
		possibilityLog.append(" all:");
		possibilityLog.append(all);
		possibilityLog.append(".");

		float totalCount = total.getCount();
		float possibility = (float) totalCount / (float) all;
		Integer weight = 1;
		ArrayList<RecordTable> relates = getRelativeRecords(context, total);
		for (RecordTable table : relates) {
			if (RecordTable.DEFAULT_COUNT_INT == table.getCount()) {
				float defaultPossibility = table.getDefaultPossibility(context
						.getContext());
				possibility *= defaultPossibility;
				possibilityLog.append(table.getClass().getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(defaultPossibility);
				possibilityLog.append(",");
			} else {
				// weight for each feature
				weight = WEIGHTS.get(table.getClass());
				weight = null == weight ? 1 : weight;
				possibility *= Math.pow(table.getCount() / totalCount, weight);
				possibilityLog.append(table.getClass().getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(table.getCount());
				possibilityLog.append(",");
			}
		}
		total.setPossibility(possibility);
		possibilityLog.append("possibility:");
		possibilityLog.append(total.getPossibility());
		LogService.d(ReadService.class, possibilityLog.toString(), context
				.getContext().getApplicationContext());
	}

	@Override
	public ArrayList<RecordTable> getRelativeRecords(PredictContext context,
			Total total) {
		ArrayList<RecordTable> records = new ArrayList<RecordTable>();
		DatabaseAccessor accessor = context.getAccessor();
		Class<? extends RecordTable>[] tables = accessor.getTables();
		for (Class<? extends RecordTable> clazz : tables) {
			RecordTable queryTable;
			try {
				queryTable = clazz.newInstance();
				queryTable.queryForRead(new RecordContext(context.getContext(),
						total));
				ArrayList<Table> allTables = accessor.R(queryTable);
				if (null != allTables) {
					records.add((RecordTable) allTables.get(0));
				} else {
					records.add(queryTable);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return records;
	}
}
