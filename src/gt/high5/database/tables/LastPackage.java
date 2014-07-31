package gt.high5.database.tables;

import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.RecordContext;
import gt.high5.core.service.RecordService;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

import java.util.List;

/**
 * @author ayi.zty
 * 
 *         Markov Chain of apps
 * 
 *         maybe extended in length
 */
public final class LastPackage extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "")
	private String lastPackage = "";

	@Override
	public boolean currentQueryStatus(RecordContext context) {
		PackageProvider provider = null;
		RecordService service = null;
		if (null != (service = context.getRecordService())
				&& null != (provider = service.getPackageProvider())) {
			Total total = context.getTotal();
			List<String> order = provider.getLastPackageOrder(context
					.getContext());
			int index = order.indexOf(total.getName());
			// exists and not last one
			if (-1 != index && order.size() - 1 != index) {
				lastPackage = order.get(index + 1);
				return true;
			}
		}
		return false;
	}

	public String getLastPackage() {
		return lastPackage;
	}

	public void setLastPackage(String lastPackage) {
		this.lastPackage = lastPackage;
	}

}
