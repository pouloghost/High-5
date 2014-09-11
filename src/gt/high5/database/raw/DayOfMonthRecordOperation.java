package gt.high5.database.raw;

import java.util.Calendar;

import gt.high5.core.service.RecordContext;

public class DayOfMonthRecordOperation implements RecordOperation {
	@Override
	public Object queryForRecord(RecordContext context) {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public Class<?> getType() {
		return Integer.class;
	}

}
