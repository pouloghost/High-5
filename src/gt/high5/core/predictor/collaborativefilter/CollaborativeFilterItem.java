package gt.high5.core.predictor.collaborativefilter;

import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableInfo;
import gt.high5.database.table.Total;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * @author GT
 * 
 *         item base collaborative filtering, holding all records
 * 
 */
public class CollaborativeFilterItem {
	HashMap<Class<? extends RecordTable>, ArrayList<Table>> mRecords = new HashMap<Class<? extends RecordTable>, ArrayList<Table>>();

	public float similarityWith(CollaborativeFilterItem item,
			DatabaseAccessor accessor) {
		Set<Class<? extends RecordTable>> otherKeys = item.mRecords.keySet();
		float similarity = 0;
		for (Class<? extends RecordTable> key : mRecords.keySet()) {
			if (Total.class != key && otherKeys.contains(key)) {
				TableInfo info = accessor.getTableInfo(key);
				// similarity comparator is null
				similarity += info.getWeight()
						* info.getSimilarityComparator().getSimilarity(
								mRecords.get(key), item.mRecords.get(key));
			}
		}
		return similarity;
	}

	public void put(Class<? extends RecordTable> key, ArrayList<Table> value) {
		mRecords.put(key, value);
	}

	public void setRecords(
			HashMap<Class<? extends RecordTable>, ArrayList<Table>> records) {
		mRecords = records;
	}

	public Collection<Table> getRelativeTables() {
		ArrayList<Table> result = new ArrayList<Table>();
		for (Class<? extends RecordTable> key : mRecords.keySet()) {
			result.addAll(mRecords.get(key));
		}
		return result;
	}
}
