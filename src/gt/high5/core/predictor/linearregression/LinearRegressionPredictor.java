package gt.high5.core.predictor.linearregression;

import gt.high5.R;
import gt.high5.core.predictor.MultiThreadPredictor;
import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.PredictorUtils;
import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.LogService;
import gt.high5.core.service.ReadService;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.ArrayMap;

public class LinearRegressionPredictor extends MultiThreadPredictor {
	private static final int XML_ID = R.xml.lr_tables;
	private static final float ALPHA = 0.01f;

	@Override
	public List<Table> predictPossibility(PredictContext context) {
		DatabaseAccessor accessor = getAccessor(context.getContext());
		// read all packages
		Total queryTotal = new Total();
		List<Table> allTotals = accessor.R(queryTotal);
		if (null != allTotals) {
			// read all counts in total
			final int all = getAllCount(accessor);

			List<String> last = PackageProvider.getPackageProvider(
					context.getContext()).getNoneCalculateZone(
					context.getContext(), 5);
			long start = System.currentTimeMillis();
			List<Callable<Total>> tasks = createTaskList(context, allTotals,
					all, last,
					LinearRegressionDataPreference.getPreference(context
							.getContext()));

			allTotals = execute(allTotals, tasks);
			LogService.d(ReadService.class,
					"time for predict " + (System.currentTimeMillis() - start),
					context.getContext());

		}
		return allTotals;
	}

	@Override
	public List<RecordTable> getRelativeRecords(PredictContext context,
			Total total) {
		return PredictorUtils.getRelativeRecordsBasedOnContext(context, total,
				getAccessor(context.getContext()), getTables());
	}

	@Override
	public float getMinThreshold() {
		return 1;
	}

	@Override
	public DatabaseAccessor getAccessor(Context context) {
		return DatabaseAccessor.getAccessor(context, XML_ID);
	}

	@Override
	public void onRecordSuccess(List<RecordTable> records, Context context) {
		Total total = null;
		for (RecordTable table : records) {
			if (table instanceof Total) {
				total = (Total) table;
			}
		}
		int all = getAllCount(getAccessor(context));
		// possibility is for each record object
		Map<Object, Float> possibilities = getPossibilities(total, records, all);
		// data is for each class
		LinearRegressionDataPreference data = LinearRegressionDataPreference
				.getPreference(context);
		float hypothesis = 0;
		for (Object key : possibilities.keySet()) {
			hypothesis += data.getTheta(key.getClass())
					* possibilities.get(key);
		}
		float diff = hypothesis - 1;
		for (Object key : possibilities.keySet()) {
			data.setTheta(key.getClass(), data.getTheta(key.getClass()) - ALPHA
					* diff * possibilities.get(key));
		}
	}

	@Override
	protected int getXmlId() {
		return XML_ID;
	}

	/**
	 * read record sum
	 * 
	 * @param accessor
	 * @return
	 */
	private int getAllCount(DatabaseAccessor accessor) {
		String column = "SUM(count)";
		Cursor cursor = accessor.query("SELECT " + column + " FROM "
				+ Total.class.getSimpleName());
		cursor.moveToFirst();
		final int all = cursor.getInt(cursor.getColumnIndex(column));
		return all;
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
			List<Table> allTotals, final int all, final List<String> last,
			final LinearRegressionDataPreference data) {
		List<Callable<Total>> tasks = new LinkedList<Callable<Total>>();
		for (Table table : allTotals) {

			final Total total = (Total) table;
			tasks.add(new Callable<Total>() {

				@Override
				public Total call() throws Exception {
					if (!last.contains((total.getName()))) {
						updatePossibility(
								new PredictContext(context.getContext(), total),
								all, data);
					}
					return total;
				}

			});
		}
		return tasks;
	}

	private void updatePossibility(PredictContext context, int all,
			LinearRegressionDataPreference data) {
		Total total = context.getTotal();
		List<RecordTable> relates = getRelativeRecords(context, total);
		Map<Object, Float> possibilities = getPossibilities(total, relates, all);
		float possibility = 0;
		StringBuilder possibilityLog = new StringBuilder("Possible ");
		possibilityLog.append(total.getName());
		possibilityLog.append(" all:");
		possibilityLog.append(all);
		possibilityLog.append(".");
		for (Object key : possibilities.keySet()) {
			// default 0
			possibilityLog.append(key.getClass().getSimpleName());
			possibilityLog.append(":");
			possibilityLog.append(possibilities.get(key));
			possibilityLog.append(",");
			possibility += data.getTheta(key.getClass())
					* possibilities.get(key);
		}
		total.setPossibility(possibility);
		LogService.d(ReadService.class, possibilityLog.toString(),
				context.getContext());
	}

	private Map<Object, Float> getPossibilities(Total total,
			List<RecordTable> records, int all) {

		ArrayMap<Object, Float> result = new ArrayMap<Object, Float>();

		float totalCount = total.getCount();
		// constant and total theta
		result.put(Integer.valueOf(0), 1f);// TODO find a way to represent
											// constant
		result.put(total, totalCount / all);
		for (RecordTable table : records) {
			if (table != total) {
				if (RecordTable.DEFAULT_COUNT_INT == table.getCount()) {
					result.put(table, 0f);
				} else {
					// theta * x
					result.put(table, table.getCount() / totalCount);
				}
			}
		}
		return result;
	}
}
