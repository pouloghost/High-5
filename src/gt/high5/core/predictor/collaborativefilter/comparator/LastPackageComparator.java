package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractStringComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.LastPackage;

import java.util.Comparator;

public class LastPackageComparator extends
		SimpleMergeCountComparator<LastPackage> {

	@Override
	public Comparator<LastPackage> getSorter() {
		return new AbstractStringComparator<LastPackage>() {

			@Override
			protected String getValue(LastPackage obj) {
				return obj.getLastPackage();
			}

		};
	}

}
