package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractIntComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.DayOfMonth;

import java.util.Comparator;

public class DayOfMonthComparator extends
		SimpleMergeCountComparator<DayOfMonth> {

	@Override
	public Comparator<DayOfMonth> getSorter() {
		return new AbstractIntComparator<DayOfMonth>() {

			@Override
			protected int getValue(DayOfMonth obj) {
				return obj.getDay();
			}
		};
	}

}
