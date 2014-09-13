package gt.high5.core.predictor.collaborativefilter.comparator;

import gt.high5.core.comparator.AbstractStringComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.WifiName;

import java.util.Comparator;

public class WifiNameComparator extends SimpleMergeCountComparator<WifiName> {

	@Override
	public Comparator<WifiName> getSorter() {
		return new AbstractStringComparator<WifiName>() {

			@Override
			protected String getValue(WifiName obj) {
				return obj.getBssid();
			}
		};
	}

}
