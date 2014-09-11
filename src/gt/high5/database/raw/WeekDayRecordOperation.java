package gt.high5.database.raw;

import java.util.Arrays;
import java.util.Calendar;

import gt.high5.core.service.RecordContext;

public class WeekDayRecordOperation implements RecordOperation {
	// week day
	private final int[] DAYS_INDEX = { Calendar.SUNDAY, Calendar.MONDAY,
			Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
			Calendar.FRIDAY, Calendar.SATURDAY, };

	@Override
	public Object queryForRecord(RecordContext context) {
		Calendar calendar = Calendar.getInstance();
		int dayValue = calendar.get(Calendar.DAY_OF_WEEK);
		return Arrays.binarySearch(DAYS_INDEX, dayValue);
	}

	@Override
	public Class<?> getType() {
		return Integer.class;
	}

}
