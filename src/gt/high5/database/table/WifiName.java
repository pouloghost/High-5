package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.raw.WifiNameRecordOperation;

/**
 * @author GT
 * 
 *         wifi bssid record
 * 
 *         indicating which ap is connected
 */
public class WifiName extends SimpleRecordTable {
	private static WifiNameRecordOperation recordOperation = new WifiNameRecordOperation();
	@TableAnnotation(defaultValue = "")
	private String bssid = "";

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return queryForRecord(context, rawRecord);
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		setPid(context.getTotal().getId());
		return checkAndSetConnection((String) rawRecord
				.getValue(RawRecord.TYPE_WIFI_NAME));
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		return checkAndSetConnection((String) recordOperation
				.queryForRecord(context));
	}

	@Override
	public float getDefaultPossibility(RecordContext context) {
		return 0.4f / context.getTotal().getCount();
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	private boolean checkAndSetConnection(String value) {
		if (null != value) {
			setBssid(value);
			return true;
		}
		return false;
	}
}
