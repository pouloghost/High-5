package gt.high5.core.predictor.collaborativefilter.comparator;

import java.util.Comparator;

import gt.high5.core.comparator.AbstractStringComparator;
import gt.high5.core.predictor.collaborativefilter.SimpleMergeCountComparator;
import gt.high5.database.table.Network;

public class NetworkComparator extends SimpleMergeCountComparator<Network> {

	@Override
	public Comparator<Network> getSorter() {
		return new AbstractStringComparator<Network>() {

			@Override
			protected String getValue(Network obj) {
				return obj.getConnection();
			}
		};
	}
}
