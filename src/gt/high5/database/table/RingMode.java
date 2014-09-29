package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.raw.RingModeRecordOperation;

public class RingMode extends SimpleRecordTable {
	private static RingModeRecordOperation recordOperation = new RingModeRecordOperation();
	@TableAnnotation(defaultValue = "-1")
	private int mode = -1;

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return queryForRecord(context, rawRecord);
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		setPid(context.getTotal().getId());
		Integer value = (Integer) rawRecord.getValue(RawRecord.TYPE_RING_MODE);
		return checkAndSetMode(value);
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		return checkAndSetMode((Integer) recordOperation
				.queryForRecord(context));
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public Object getValue() {
		return getMode();
	}

	private boolean checkAndSetMode(Integer value) {
		if (null != value) {
			setMode(value.intValue());
			return true;
		}
		return false;
	}

}
