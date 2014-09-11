package gt.high5.database.raw;

import java.util.Calendar;

import gt.high5.core.service.RecordContext;

public class TimeRecordOperation implements RecordOperation {
	// time region length in minutes
	private static int regionLength = 15;

	public static void setRegionLength(int length) {
		regionLength = length;
	}

	@Override
	public Object queryForRecord(RecordContext context) {
		Calendar calendar = Calendar.getInstance();
		int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60
				+ calendar.get(Calendar.MINUTE);
		return (int) (minutes / regionLength);
	}

	@Override
	public Class<?> getType() {
		return Integer.class;
	}

}
