package gt.high5.core.comparator;

import java.util.Comparator;

public abstract class AbstractStringComparator<T> implements Comparator<T> {

	protected abstract String getValue(T obj);

	@Override
	public int compare(T a, T b) {
		return getValue(a).compareTo(getValue(b));
	}

}
