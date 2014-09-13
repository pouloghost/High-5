package gt.high5.core.predictor.collaborativefilter;

import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public abstract class SimpleMergeCountComparator<T> implements
		SimilarityComparator<T> {
	@Override
	public float getSimilarity(ArrayList<Table> a, ArrayList<Table> b) {
		int total = 0;
		int same = 0;

		// merge two lists
		Iterator<Table> aIterator = a.iterator();
		Iterator<Table> bIterator = b.iterator();
		RecordTable ac = getNext(aIterator), bc = getNext(bIterator);
		Comparator<T> comparator = getSorter();

		while (null != ac && null != bc) {
			@SuppressWarnings("unchecked")
			int state = comparator.compare((T) ac, (T) bc);
			if (state < 0) {// a is smaller
				total += ac.getCount();
				ac = getNext(aIterator);
			} else if (0 == state) {
				// same data
				total += ac.getCount();
				total += bc.getCount();
				same += ac.getCount();
				same += bc.getCount();

				ac = getNext(aIterator);
				bc = getNext(bIterator);
			} else if (state > 0) {
				total += bc.getCount();
				bc = getNext(bIterator);
			}
		}

		if (null == ac) {
			while (null != bc) {
				total += bc.getCount();
				bc = getNext(bIterator);
			}
		}

		if (null == bc) {
			while (null != ac) {
				total += ac.getCount();
				ac = getNext(aIterator);
			}
		}
		return (float) same / (float) total;
	}

	private RecordTable getNext(Iterator<Table> iterator) {
		if (iterator.hasNext()) {
			return (RecordTable) iterator.next();
		}
		return null;
	}
}
