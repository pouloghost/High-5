package gt.high5.core.predictor.collaborativefilter;

import gt.high5.R;
import gt.high5.core.predictor.MultiThreadPredictor;
import gt.high5.core.predictor.PredictContext;
import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.LogService;
import gt.high5.core.service.ReadService;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;

public class CollaborativeFilterPredictor extends MultiThreadPredictor {
	private static final int XML_ID = R.xml.cf_tables;
	private static final int RELATIVE_TASK_SIZE = 6;
	private static final int MIN_THRESHOLD = RELATIVE_TASK_SIZE * 6;

	@Override
	public List<Table> predictPossibility(PredictContext context) {
		// read five recent packages
		PackageProvider provider = PackageProvider.getPackageProvider(context
				.getContext());
		List<String> lastApps = provider.getNoneCalculateZone(
				context.getContext(), RELATIVE_TASK_SIZE);
		if (null == lastApps) {
			return null;
		}

		// build up item of five recent apps
		final DatabaseAccessor accessor = getAccessor(context.getContext());
		Total queryTotal = new Total();
		final List<CollaborativeFilterItem> lastItems = new LinkedList<CollaborativeFilterItem>();
		for (String name : lastApps) {
			queryTotal.setName(name);
			List<Table> totalList = accessor.R(queryTotal);
			if (null != totalList) {
				queryTotal = (Total) totalList.get(0);
			}
			CollaborativeFilterItem item = CollaborativeFilterItem.buildItem(
					accessor, queryTotal);
			if (null != item) {
				lastItems.add(item);
			}
		}
		// item-cf all the packages recorded
		queryTotal = new Total();
		List<Table> allTotals = accessor.R(queryTotal);
		if (null != allTotals) {
			long start = System.currentTimeMillis();
			List<Callable<Total>> tasks = createTaskList(lastApps, accessor,
					lastItems, allTotals);

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
		return null;
	}

	@Override
	public DatabaseAccessor getAccessor(Context context) {
		return DatabaseAccessor.getAccessor(context, XML_ID);
	}

	@Override
	public float getMinThreshold() {
		return MIN_THRESHOLD;
	}

	/**
	 * create task for each total
	 * 
	 * @param lastApps
	 * @param accessor
	 * @param lastItems
	 * @param allTotals
	 * @return
	 */
	private List<Callable<Total>> createTaskList(final List<String> lastApps,
			final DatabaseAccessor accessor,
			final List<CollaborativeFilterItem> lastItems, List<Table> allTotals) {
		List<Callable<Total>> tasks = new LinkedList<Callable<Total>>();
		for (Table table : allTotals) {
			final Total total = (Total) table;
			tasks.add(new Callable<Total>() {

				@Override
				public Total call() throws Exception {
					if (!lastApps.contains((total.getName()))) {// avoid
						// recommanding
						// recent 5
						CollaborativeFilterItem item = CollaborativeFilterItem
								.buildItem(accessor, total);
						float score = 0;
						for (CollaborativeFilterItem last : lastItems) {
							score += item.similarityWith(last, accessor);
						}
						total.setPossibility(score);
					}
					return total;
				}
			});
		}
		return tasks;
	}
}
