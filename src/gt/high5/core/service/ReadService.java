package gt.high5.core.service;

import gt.high5.R;
import gt.high5.core.predictor.PredictContext;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         service for read records without specified type
 */
public class ReadService {

	private static float MIN_POSSIBILITY = 1E-3f;
	// singleton
	private static ReadService instance = null;

	private DatabaseAccessor mAccessor = null;

	private ReadService(Context context) {
		if (null == mAccessor) {
			mAccessor = DatabaseAccessor.getAccessor(context, R.xml.tables);
		}
	}

	public static ReadService getReadService(Context context) {
		if (null == instance) {
			instance = new ReadService(context);
		}
		return instance;
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
			PredictContext predictContext = new PredictContext(mAccessor,
					context);
			ArrayList<Table> allTotals = mAccessor.getPredictor()
					.predictPossibility(predictContext);
			if (null != allTotals) {
				Collections.sort(allTotals, Total.getComparator());

				StringBuilder sortLog = new StringBuilder("after sort ");
				for (Table total : allTotals) {
					sortLog.append(((Total) total).getName());
					sortLog.append(":");
					sortLog.append(((Total) total).getPossibility());
					sortLog.append("\t");
				}
				LogService.d(ReadService.class, sortLog.toString(),
						context.getApplicationContext());

				HashSet<String> ignoredSet = IgnoreSetService
						.getIgnoreSetService(context).getIgnoreSet(mAccessor);

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
		} else {
			LogService.d(ReadService.class, "data accessor is null",
					context.getApplicationContext());
		}
		return last;
	}

}
