package gt.high5.core.predictor.collaborativefilter;

import gt.high5.database.model.RecordTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public abstract class SimpleMergeCountComparator<T> implements
		SimilarityComparator<RecordTable, T> {
	@Override
	public float getSimilarity(ArrayList<RecordTable> a,
			ArrayList<RecordTable> b) {
		int total = 0;
		int same = 0;
		Comparator<T> comparator = getSorter();

		// merge two lists
		Iterator<RecordTable> aIterator = a.iterator();
		Iterator<RecordTable> bIterator = b.iterator();
		RecordTable ac = getNext(aIterator), bc = getNext(bIterator);

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

	private RecordTable getNext(Iterator<RecordTable> iterator) {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
