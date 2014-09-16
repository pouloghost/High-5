package gt.high5.core.service;

import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.Predictor;
import gt.high5.core.provider.PackageProvider;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         service for read records without specified type from current
 *         predictor's database
 */
public class ReadService {

	// singleton
	private static ReadService mInstance = null;

	private Context mContext = null;

	private ArrayList<String> mLastHigh5 = new ArrayList<String>();
	private float mHit = 0;
	private float mWrong = 0;
	private float mMiss = 0;

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
	public ArrayList<String> getHigh5() throws InstantiationException,
			IllegalAccessException {
		DatabaseAccessor accessor = Predictor.getPredictor().getAccessor(
				mContext);
		float minThreshold = Predictor.getPredictor().getMinThreshold();
		if (null != accessor) {
			PredictContext predictContext = new PredictContext(mContext);
			List<Table> allTotals = Predictor.getPredictor()
					.predictPossibility(predictContext);
			if (null != allTotals) {
				updateScore();
				// sort
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

				eliminateIgnored(accessor, minThreshold, allTotals);
			}
		} else {
			LogService.d(ReadService.class, "data accessor is null",
					mContext.getApplicationContext());
		}
		return mLastHigh5;
	}

	public List<Table> getAll() {
		PredictContext predictContext = new PredictContext(mContext);
		return Predictor.getPredictor().predictPossibility(predictContext);
	}

	public float getRecallRate() {
		return mHit / (mHit + mMiss);
	}

	public float getAccuracy() {
		return mHit / (mHit + mWrong);
	}

	private void eliminateIgnored(DatabaseAccessor accessor,
			float minThreshold, List<Table> allTotals) {
		HashSet<String> ignoredSet = IgnoreSetService
				.getIgnoreSetService(mContext).getIgnoreSet(accessor);
	
		int listSize = allTotals.size();
		int size = Math.min(5, listSize);
		mLastHigh5 = new ArrayList<String>(size);
		for (int i = 0, j = 0; j < size && i < listSize; ++i) {
			if (minThreshold > ((Total) allTotals.get(i))
					.getPossibility()) {// nearly impossible
				break;
			}
			String name = ((Total) allTotals.get(i)).getName();
			if (!ignoredSet.contains(name)) {
				mLastHigh5.add(name);
				++j;
			}
		}
	}

	private void updateScore() {
		ArrayList<String> last = mLastHigh5;
		List<String> changes = PackageProvider.getPackageProvider(mContext)
				.getLastPackageOrder(mContext);
		int recommandSize = last.size();
		int changeSize = changes.size();
		last.retainAll(changes);
		int hit = last.size();
		mHit += hit;
		mWrong += (recommandSize - hit);
		mMiss += (changeSize - hit);
	}
}
