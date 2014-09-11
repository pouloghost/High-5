package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.NetworkRecordOperation;
import gt.high5.database.raw.RawRecord;

public class Network extends SimpleRecordTable {

	private static NetworkRecordOperation recordOperation = new NetworkRecordOperation();

	@TableAnnotation(defaultValue = "")
	private String connection = "";

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return queryForRecord(context, rawRecord);
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		setPid(context.getTotal().getId());
		String value = (String) rawRecord.getValue(RawRecord.TYPE_NETWORK);
		return checkAndSetConnection(value);
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		String value = (String) recordOperation.queryForRecord(context);
		return checkAndSetConnection(value);
	}

	@Override
	public float getDefaultPossibility(RecordContext context) {
		return 0.5f / context.getTotal().getCount();
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	private boolean checkAndSetConnection(String value) {
		if (null != value) {
			setConnection(value);
			return true;
		}
		return false;
	}
}
