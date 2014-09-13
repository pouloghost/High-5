package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractIntComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.WeekDay;

import java.util.Comparator;

public class WeekDayComparator extends SimpleMergeCountComparator<WeekDay> {

	@Override
	public Comparator<WeekDay> getSorter() {
		return new AbstractIntComparator<WeekDay>() {

			@Override
			protected int getValue(WeekDay obj) {
				return obj.getDay();
			}
		};
	}

}
