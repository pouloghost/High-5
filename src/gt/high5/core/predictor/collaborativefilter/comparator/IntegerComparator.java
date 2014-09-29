package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractIntComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.model.RecordTable;

import java.util.Comparator;

public class IntegerComparator extends SimpleMergeCountComparator<RecordTable> {

	@Override
	public Comparator<RecordTable> getSorter() {
		return new AbstractIntComparator<RecordTable>() {

			@Override
			protected int getValue(RecordTable obj) {
				return (Integer) obj.getValue();
			}
		};
	}

}
