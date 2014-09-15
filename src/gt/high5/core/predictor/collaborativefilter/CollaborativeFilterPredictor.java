package gt.high5.core.predictor.collaborativefilter;

import gt.high5.R;
import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.Predictor;
import gt.high5.core.provider.PackageProvider;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

public class CollaborativeFilterPredictor extends Predictor {
	private static int XML_ID = R.xml.cf_tables;

	@Override
	public List<Table> predictPossibility(PredictContext context) {
		// read five recent packages
		PackageProvider provider = PackageProvider.getPackageProvider(context
				.getContext());
		List<String> lastApps = provider.getNoneCalculateZone(context
				.getContext());
		if (null == lastApps) {
			return null;
		}

		// build up item of five recent apps
		DatabaseAccessor accessor = getAccessor(context.getContext());
		Total queryTotal = new Total();
		List<CollaborativeFilterItem> lastItems = new LinkedList<CollaborativeFilterItem>();
		for (String name : lastApps) {
			queryTotal.setName(name);
			List<Table> totalList = accessor.R(queryTotal);
			if (null != totalList) {
				queryTotal = (Total) totalList.get(0);
			}
			CollaborativeFilterItem item = buildItem(accessor, queryTotal);
			if (null != item) {
				lastItems.add(item);
			}
		}
		// item-cf all the packages recorded
		queryTotal = new Total();
		List<Table> allTotals = accessor.R(queryTotal);
		if (null != allTotals) {
			for (Table total : allTotals) {
				if (!lastApps.contains(((Total) total).getName())) {// avoid
																	// recommanding
																	// recent 5
					CollaborativeFilterItem item = buildItem(accessor,
							(Total) total);
					float score = 0;
					for (CollaborativeFilterItem last : lastItems) {
						score += last.similarityWith(item, accessor);
					}
					((Total) total).setPossibility(score);
				}
			}
		}
		return allTotals;
	}

	@Override
	public Collection<RecordTable> getRelativeRecords(PredictContext context,
			Total total) {
		return null;
	}

	@Override
	public DatabaseAccessor getAccessor(Context context) {
		return DatabaseAccessor.getAccessor(context, XML_ID);
	}

	@Override
	public float getMinThreshold() {
		return 30;
	}

	private CollaborativeFilterItem buildItem(DatabaseAccessor accessor,
			Total queryTotal) {
		RecordTable queryTable = null;
		CollaborativeFilterItem item = new CollaborativeFilterItem();
		for (Class<? extends RecordTable> clazz : accessor.getTables()) {
			if (Total.class != clazz) {// total not considered
				try {
					queryTable = clazz.newInstance();
					queryTable.setPid(queryTotal.getId());
					List<Table> tableList = accessor.R(queryTable);
					if (null != tableList) {
						item.put(clazz, tableList);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return item;
	}

}
