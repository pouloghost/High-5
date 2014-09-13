package gt.high5.core.service;

import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.Predictor;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.table.nb.Total;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         service for read records without specified type
 */
public class ReadService {

	private static float MIN_POSSIBILITY = 1E-20f;
	// singleton
	private static ReadService mInstance = null;

	private Context mContext = null;

	// private DatabaseAccessor mAccessor = null;

	private ReadService(Context context) {
		mContext = context;
	}

	public static ReadService getReadService(Context context) {
		if (null == mInstance) {
			synchronized (ReadService.class) {
				if (null == mInstance) {
					mInstance = new ReadService(context);
				}
			}
		}
		return mInstance;
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
	public ArrayList<String> getHigh5(ArrayList<String> last)
			throws InstantiationException, IllegalAccessException {
		DatabaseAccessor accessor = Predictor.getPredictor().getAccessor(
				mContext);
		if (null != accessor) {
			last.clear();
			PredictContext predictContext = new PredictContext(mContext);
			ArrayList<Table> allTotals = Predictor.getPredictor()
					.predictPossibility(predictContext);
			if (null != allTotals) {
				Collections.sort(allTotals, new Comparator<Table>() {
					@Override
					public int compare(Table arg0, Table arg1) {
						float p0 = ((Total) arg0).getPossibility();
						float p1 = ((Total) arg1).getPossibility();
						return p1 > p0 ? 1 : p1 == p0 ? 0 : -1;
					}
				});

				StringBuilder sortLog = new StringBuilder("after sort ");
				for (Table total : allTotals) {
					sortLog.append(((Total) total).getName());
					sortLog.append(":");
					sortLog.append(((Total) total).getPossibility());
					sortLog.append("\t");
				}
				LogService.d(ReadService.class, sortLog.toString(),
						mContext.getApplicationContext());

				HashSet<String> ignoredSet = IgnoreSetService
						.getIgnoreSetService(mContext).getIgnoreSet(accessor);

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
					mContext.getApplicationContext());
		}
		return last;
	}
}
