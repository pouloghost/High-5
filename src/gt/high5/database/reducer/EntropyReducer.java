package gt.high5.database.reducer;

import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.support.v4.util.ArrayMap;

public class EntropyReducer extends Reducer {
	private static double THRESHOLD = 1d;

	@Override
	public List<Class<? extends RecordTable>> shouldRead(
			Map<Class<? extends RecordTable>, List<Table>> records) {
		LinkedList<Class<? extends RecordTable>> results = new LinkedList<Class<? extends RecordTable>>();
		for (Class<? extends RecordTable> clazz : records.keySet()) {
			double entropy = getEntropyOfTable(records.get(clazz));
			if (entropy > THRESHOLD) {
				results.add(clazz);
			}
		}
		return results;
	}

	private double getEntropyOfTable(List<Table> list) {
		ArrayMap<Object, Integer> accumulation = new ArrayMap<Object, Integer>();
		double all = 0;
		for (Table table : list) {
			RecordTable recordTable = (RecordTable) table;
			Object value = recordTable.getValue();
			Integer sum = accumulation.get(value);
			sum = null == sum ? recordTable.getCount() : sum
					+ recordTable.getCount();
			accumulation.put(value, sum);
			all += recordTable.getCount();
		}
		double entropy = 0;
		double base = Math.log(2d);
		for (Object key : accumulation.keySet()) {
			double p = accumulation.get(key) / all;
			entropy -= p * Math.log(p) / base;
		}
		return entropy;
	}
}
