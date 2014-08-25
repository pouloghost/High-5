package gt.high5.core.predictor;

import gt.high5.core.service.LogService;
import gt.high5.core.service.ReadService;
import gt.high5.core.service.RecordContext;
import gt.high5.core.service.RecordService;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

/**
 * @author GT
 * 
 *         naive bayes
 */
public class NaiveBayesPredictor implements Predictor {

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

		RecordService service = null;
		try {
			service = RecordService.getRecordService(context.getContext());
			Class<? extends RecordTable>[] tables = accessor.getTables();
			if (null != allTotals) {
				for (Table total : allTotals) {
					updatePossibility(context.getContext(), all, service,
							tables, (Total) total, context.getAccessor());
				}
			}
		} catch (Exception e) {
			allTotals = null;
			e.printStackTrace();
		}
		return allTotals;
	}

	private void updatePossibility(Context context, int all,
			RecordService service, Class<? extends RecordTable>[] tables,
			Total total, DatabaseAccessor accessor)
			throws InstantiationException, IllegalAccessException {
		StringBuilder possibilityLog = new StringBuilder("Possible ");
		possibilityLog.append(total.getName());
		possibilityLog.append(" all:");
		possibilityLog.append(all);
		possibilityLog.append(".");

		int totalCount = total.getCount();
		float possibility = (float) totalCount / (float) all;
		for (Class<? extends RecordTable> clazz : tables) {
			RecordTable queryTable = clazz.newInstance();
			queryTable.currentQueryStatus(new RecordContext(context, service,
					total));
			ArrayList<Table> allTables = accessor.R(queryTable);
			if (null != allTables) {
				possibility *= (float) ((RecordTable) allTables.get(0))
						.getCount() / totalCount;
				possibilityLog.append(((RecordTable) allTables.get(0))
						.getClass().getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(((RecordTable) allTables.get(0))
						.getCount());
				possibilityLog.append(",");
			} else {
				// no existing record meaning user won't use this
				// app in current condition
				possibility *= queryTable.getDefaultPossibility(context);
				possibilityLog.append(((RecordTable) queryTable).getClass()
						.getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(((RecordTable) queryTable)
						.getDefaultPossibility(context));
				possibilityLog.append(",");
			}
		}
		total.setPossibility(possibility);
		possibilityLog.append("possibility:");
		possibilityLog.append(total.getPossibility());
		LogService.d(ReadService.class, possibilityLog.toString(),
				context.getApplicationContext());
	}
}
