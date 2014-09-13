package gt.high5.database.table;

import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.RawRecord;

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

	/*
	 * @see
	 * gt.high5.database.model.SimpleRecordTable#initDefault(gt.high5.core.service
	 * .RecordContext)
	 * 
	 * set package as the package next to total.package
	 */
	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return queryForRecord(context, rawRecord);
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		setPid(context.getTotal().getId());
		String value = (String) rawRecord.getValue(RawRecord.TYPE_LAST_PACKAGE);
		if (null != value) {
			setLastPackage(value);
			return true;
		}
		return false;
	}

	/*
	 * @see
	 * gt.high5.database.model.RecordTable#currentQueryStatus(gt.high5.core.
	 * service.RecordContext)
	 * 
	 * set the first package in recent list
	 */
	@Override
	public boolean queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		PackageProvider provider = null;
		List<String> order = null;
		if (null != (provider = PackageProvider.getPackageProvider(context
				.getContext()))
				&& null != (order = provider.getLastPackageOrder(context
						.getContext()))) {
			if (order.size() > 0) {
				lastPackage = order.get(0);
				setPid(context.getTotal().getId());
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
