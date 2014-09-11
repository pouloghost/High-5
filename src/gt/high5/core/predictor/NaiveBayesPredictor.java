package gt.high5.core.predictor;

import gt.high5.R;
import gt.high5.core.service.LogService;
import gt.high5.core.service.ReadService;
import gt.high5.core.service.RecordContext;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.nb.Total;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

/**
 * @author GT
 * 
 *         naive bayes
 */
public class NaiveBayesPredictor extends Predictor {

	private static int XML_ID = R.xml.tables;

	@Override
	public ArrayList<Table> predictPossibility(PredictContext context) {
		DatabaseAccessor accessor = getAccessor(context.getContext());
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
		ArrayList<RecordTable> relates = getRelativeRecords(context, total);
		for (RecordTable table : relates) {
			if (RecordTable.DEFAULT_COUNT_INT == table.getCount()) {
				float defaultPossibility = table
						.getDefaultPossibility(new RecordContext(context
								.getContext(), total));
				possibility *= defaultPossibility;
				possibilityLog.append(table.getClass().getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(defaultPossibility);
				possibilityLog.append(",");
			} else {
				// weight for each feature
				possibility *= Math.pow(
						table.getCount() / totalCount,
						getAccessor(context.getContext()).getTableWeight(
								table.getClass()));
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
		DatabaseAccessor accessor = getAccessor(context.getContext());
		Class<? extends RecordTable>[] tables = accessor.getTables();
		for (Class<? extends RecordTable> clazz : tables) {
			RecordTable queryTable;
			try {
				queryTable = clazz.newInstance();
				if (queryTable.queryForRead(new RecordContext(context
						.getContext(), total))) {
					ArrayList<Table> allTables = accessor.R(queryTable);
					if (null != allTables) {// available record
						queryTable = (RecordTable) allTables.get(0);
					}
				}
				records.add(queryTable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return records;
	}

	@Override
	public DatabaseAccessor getAccessor(Context context) {
		return DatabaseAccessor.getAccessor(context, XML_ID);
	}
}
