package gt.high5.database.raw;

import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.RecordContext;
import gt.high5.database.table.nb.Total;

import java.util.List;

public class LastPackageRecordOperation implements RecordOperation {
	@Override
	public Object queryForRecord(RecordContext context) {
		PackageProvider provider = null;
		List<String> order = null;
		if (null != (provider = PackageProvider.getPackageProvider(context
				.getContext()))
				&& null != (order = provider.getLastPackageOrder(context
						.getContext()))) {
			Total total = context.getTotal();
			int index = order.indexOf(total.getName());
			// exists and not last one
			if (-1 != index && order.size() - 1 != index) {
				return order.get(index + 1);
			}
		}
		return null;
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}
}
