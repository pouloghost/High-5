package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.raw.TimeRecordOperation;

/**
 * @author GT
 * 
 *         time region statics
 */
public class Time extends SimpleRecordTable {

	private static final int END = 3;
	private static final int BACKWARD = 1;
	private static TimeRecordOperation recordOperation = new TimeRecordOperation();
	@TableAnnotation(defaultValue = "-1")
	private int region = -1;
	@TableAnnotation(isTransient = true)
	private int state = 0;

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return queryForRecord(context, rawRecord);
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		setPid(context.getTotal().getId());
		return checkAndSetRegion((Integer) rawRecord
				.getValue(RawRecord.TYPE_TIME));
	}

	@Override
	public int queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		Integer value = (Integer) recordOperation.queryForRecord(context);
		if (null == value) {
			return READ_FAILED;
		}
		setRegion(value - BACKWARD + state);
		++state;
		return END == state ? READ_DONE : READ_CONTINUE;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	@Override
	public Object getValue() {
		return getRegion();
	}

	private boolean checkAndSetRegion(Integer value) {
		if (null != value) {
			setRegion(value);
			return true;
		}
		return false;
	}
}
