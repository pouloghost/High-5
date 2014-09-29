package gt.high5.core.predictor.naivebayes;

import gt.high5.R;
import gt.high5.core.predictor.MultiThreadPredictor;
import gt.high5.core.predictor.PredictContext;
import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.LogService;
import gt.high5.core.service.ReadService;
import gt.high5.core.service.RecordContext;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.database.Cursor;

/**
 * @author GT
 * 
 *         naive bayes
 */
public class NaiveBayesPredictor extends MultiThreadPredictor {

	private static final int XML_ID = R.xml.nb_tables;

	@Override
	public List<Table> predictPossibility(final PredictContext context) {
		DatabaseAccessor accessor = getAccessor(context.getContext());
		// read all packages
		Total queryTotal = new Total();
		List<Table> allTotals = accessor.R(queryTotal);
		// read all counts in total
		String column = "SUM(count)";
		Cursor cursor = accessor.query("SELECT " + column + " FROM "
				+ Total.class.getSimpleName());
		cursor.moveToFirst();
		final int all = cursor.getInt(cursor.getColumnIndex(column));

		List<String> last = PackageProvider.getPackageProvider(
				context.getContext()).getNoneCalculateZone(
				context.getContext(), 5);

		if (null != allTotals) {
			long start = System.currentTimeMillis();
			List<Callable<Total>> tasks = createTaskList(context, allTotals,
					all, last);

			allTotals = execute(allTotals, tasks);
			LogService.d(ReadService.class,
					"time for predict " + (System.currentTimeMillis() - start),
					context.getContext());
		}

		return allTotals;
	}

	@Override
	public Collection<RecordTable> getRelativeRecords(PredictContext context,
			Total total) {
		ArrayList<RecordTable> records = new ArrayList<RecordTable>();
		DatabaseAccessor accessor = getAccessor(context.getContext());
		Class<? extends RecordTable>[] tables = getTables();
		for (Class<? extends RecordTable> clazz : tables) {
			RecordTable queryTable;
			try {
				queryTable = clazz.newInstance();
				if (queryTable.queryForRead(new RecordContext(context
						.getContext(), total))) {
					List<Table> allTables = accessor.R(queryTable);
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

	@Override
	public float getMinThreshold() {
		return getTables().length / 3;
	}

	@Override
	protected int getXmlId() {
		return XML_ID;
	}

	/**
	 * create task for each total
	 * 
	 * @param context
	 * @param allTotals
	 * @param all
	 * @param last
	 * @return
	 */
	private List<Callable<Total>> createTaskList(final PredictContext context,
			List<Table> allTotals, final int all, final List<String> last) {
		List<Callable<Total>> tasks = new LinkedList<Callable<Total>>();
		for (Table table : allTotals) {

			final Total total = (Total) table;
			tasks.add(new Callable<Total>() {

				@Override
				public Total call() throws Exception {
					if (!last.contains((total.getName()))) {
						updatePossibility(
								new PredictContext(context.getContext(), total),
								all);
					}
					return total;
				}
			});
		}
		return tasks;
	}

	/**
	 * calculate a possibility using naive bayes upon a total
	 * 
	 * @param context
	 * @param all
	 */
	private void updatePossibility(PredictContext context, int all) {
		Total total = context.getTotal();
		StringBuilder possibilityLog = new StringBuilder("Possible ");
		possibilityLog.append(total.getName());
		possibilityLog.append(" all:");
		possibilityLog.append(all);
		possibilityLog.append(".");

		float totalCount = total.getCount();
		float possibility = (float) totalCount / (float) all;
		// punish records that appears quite occasionally
		possibility = (float) Math.pow(possibility, 9.0f);
		Collection<RecordTable> relates = getRelativeRecords(context, total);
		for (RecordTable table : relates) {
			if (RecordTable.DEFAULT_COUNT_INT == table.getCount()) {
				float defaultPossibility = getTableInfo(table.getClass())
						.getNaiveBayesData()
						.getDefaultPossibility(
								new PredictContext(context.getContext(), total));
				possibility *= defaultPossibility;
				possibilityLog.append(table.getClass().getSimpleName());
				possibilityLog.append(":");
				possibilityLog.append(defaultPossibility);
				possibilityLog.append(",");
			} else {
				// weight for each feature
				possibility *= Math.pow(table.getCount() / totalCount,
						getTableWeight(table.getClass()));
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
}
