package gt.high5.core.predictor.collaborativefilter.comparator;

import java.util.Comparator;

import gt.high5.core.comparator.AbstractIntComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.RingMode;

public class RingModeComparator extends SimpleMergeCountComparator<RingMode> {

	@Override
	public Comparator<RingMode> getSorter() {
		return new AbstractIntComparator<RingMode>() {

			@Override
			protected int getValue(RingMode obj) {
				return obj.getMode();
			}
		};
	}

}
