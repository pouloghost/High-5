package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractStringComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.model.RecordTable;

import java.util.Comparator;

public class StringComparator extends SimpleMergeCountComparator<RecordTable> {

	@Override
	public Comparator<RecordTable> getSorter() {
		return new AbstractStringComparator<RecordTable>() {

			@Override
			protected String getValue(RecordTable obj) {
				return (String) obj.getValue();
			}

		};
	}

}
