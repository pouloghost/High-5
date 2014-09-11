package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.DayOfMonthRecordOperation;
import gt.high5.database.raw.RawRecord;

public class DayOfMonth extends SimpleRecordTable {

	private static DayOfMonthRecordOperation recordOperation = new DayOfMonthRecordOperation();

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
		Integer value = (Integer) rawRecord
				.getValue(RawRecord.TYPE_DAY_OF_MONTH);
		return checkAndSetDay(value);
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		return checkAndSetDay((Integer) recordOperation.queryForRecord(context));
	}

	@Override
	public float getDefaultPossibility(RecordContext context) {
		return 0.3f / context.getTotal().getCount();
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	private boolean checkAndSetDay(Integer value) {
		if (null != value) {
			setDay(value.intValue());
			return true;
		}
		return false;
	}

}
