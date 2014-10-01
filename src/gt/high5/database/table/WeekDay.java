package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.raw.WeekDayRecordOperation;

public class WeekDay extends SimpleRecordTable {

	private static WeekDayRecordOperation recordOperation = new WeekDayRecordOperation();
	@TableAnnotation(defaultValue = "-1")
	private int day = -1;

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return queryForRecord(context, rawRecord);
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		setPid(context.getTotal().getId());
		return checkAndSetDay((Integer) rawRecord
				.getValue(RawRecord.TYPE_WEEK_DAY));
	}

	@Override
	public int queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		return checkAndSetDay((Integer) recordOperation.queryForRecord(context)) ? READ_DONE
				: READ_FAILED;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	@Override
	public Object getValue() {
		return getDay();
	}

	private boolean checkAndSetDay(Integer value) {
		if (null != value) {
			setDay(value.intValue());
			return true;
		}
		return false;
	}
}
