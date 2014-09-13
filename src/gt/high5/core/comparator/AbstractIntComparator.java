package gt.high5.core.comparator;

import java.util.Comparator;

public abstract class AbstractIntComparator<T> implements Comparator<T> {

	protected abstract int getValue(T obj);

	@Override
	public int compare(T a, T b) {
		return getValue(a) - getValue(b);
	}

}
