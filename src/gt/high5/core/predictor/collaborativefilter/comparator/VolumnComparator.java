package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractIntComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.AbstractVolumn;

import java.util.Comparator;

public class VolumnComparator extends
		SimpleMergeCountComparator<AbstractVolumn> {

	@Override
	public Comparator<AbstractVolumn> getSorter() {
		return new AbstractIntComparator<AbstractVolumn>() {

			@Override
			protected int getValue(AbstractVolumn obj) {
				return obj.getPercent();
			}
		};
	}

}
