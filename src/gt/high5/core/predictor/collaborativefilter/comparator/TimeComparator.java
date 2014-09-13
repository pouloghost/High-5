package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractIntComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.Time;

import java.util.Comparator;

public class TimeComparator extends SimpleMergeCountComparator<Time> {

	@Override
	public Comparator<Time> getSorter() {
		return new AbstractIntComparator<Time>() {

			@Override
			protected int getValue(Time obj) {
				return obj.getRegion();
			}

		};
	}

}
