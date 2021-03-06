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
	public int queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		return checkAndSetConnection((String) recordOperation
				.queryForRecord(context)) ? READ_DONE : READ_FAILED;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	@Override
	public Object getValue() {
		return getBssid();
	}

	private boolean checkAndSetConnection(String value) {
		if (null != value) {
			setBssid(value);
			return true;
		}
		return false;
	}
}
