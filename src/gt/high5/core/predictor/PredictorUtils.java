package gt.high5.core.predictor;

import gt.high5.core.service.RecordContext;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.LinkedList;
import java.util.List;

public final class PredictorUtils {
	/**
	 * read all records using queryForRead
	 * 
	 * @param context
	 * @param total
	 * @param accessor
	 * @param tables
	 * @return
	 */
	public static List<RecordTable> getRelativeRecordsBasedOnContext(
			PredictContext context, Total total, DatabaseAccessor accessor,
			Class<? extends RecordTable>[] tables) {
		LinkedList<RecordTable> records = new LinkedList<RecordTable>();
		RecordContext recordContext = new RecordContext(context.getContext(),
				total);
		for (Class<? extends RecordTable> clazz : tables) {
			RecordTable queryTable;
			try {
				queryTable = clazz.newInstance();
				int state = RecordTable.READ_FAILED;
				do {
					state = queryTable.queryForRead(recordContext);
					if (RecordTable.READ_FAILED == state) {
						break;
					}
					List<Table> allTables = accessor.R(queryTable);
					if (null != allTables) {// available record
						records.add((RecordTable) allTables.get(0));
					} else {
						records.add(queryTable);
					}
					recordContext.recordNext();
				} while (RecordTable.READ_CONTINUE == state);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return records;
	}
}
