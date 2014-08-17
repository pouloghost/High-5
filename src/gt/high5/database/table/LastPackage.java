package gt.high5.database.table;

import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.RecordContext;
import gt.high5.core.service.RecordService;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

import java.util.List;

import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         Markov Chain of apps
 * 
 *         maybe extended in length
 */
public final class LastPackage extends SimpleRecordTable {

	private static String title = "";
	
	@TableAnnotation(defaultValue = "")
	private String lastPackage = "";

	/*
	 * @see
	 * gt.high5.database.model.RecordTable#currentQueryStatus(gt.high5.core.
	 * service.RecordContext)
	 * 
	 * set the first package in recent list
	 */
	@Override
	public boolean currentQueryStatus(RecordContext context) {
		PackageProvider provider = null;
		RecordService service = null;
		List<String> order = null;
		if (null != (service = context.getRecordService())
				&& null != (provider = service.getPackageProvider())
				&& null != (order = provider.getLastPackageOrder(context
						.getContext()))) {
			if (order.size() > 0) {
				lastPackage = order.get(0);
				return true;
			}
		}
		return false;
	}

	/*
	 * @see
	 * gt.high5.database.model.SimpleRecordTable#initDefault(gt.high5.core.service
	 * .RecordContext)
	 * 
	 * set package as the package next to total.package
	 */
	@Override
	public boolean initDefault(RecordContext context) {
		PackageProvider provider = null;
		RecordService service = null;
		List<String> order = null;
		if (null != (service = context.getRecordService())
				&& null != (provider = service.getPackageProvider())
				&& null != (order = provider.getLastPackageOrder(context
						.getContext()))) {
			count = 1;
			Total total = context.getTotal();
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

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.1f;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		LastPackage.title = title;
	}
}
