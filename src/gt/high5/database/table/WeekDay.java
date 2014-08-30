package gt.high5.database.table;

import java.util.Arrays;
import java.util.Calendar;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import android.content.Context;

public class WeekDay extends SimpleRecordTable {

	private static final int[] DAYS_INDEX = { Calendar.SUNDAY, Calendar.MONDAY,
			Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
			Calendar.FRIDAY, Calendar.SATURDAY, };

	@TableAnnotation(defaultValue = "-1")
	private int day = -1;

	@Override
	public boolean initDefault(RecordContext context) {
		count = 1;
		return queryForRecord(context);
	}

	@Override
	public boolean queryForRecord(RecordContext context) {
		Calendar calendar = Calendar.getInstance();
		int dayValue = calendar.get(Calendar.DAY_OF_WEEK);
		day = Arrays.binarySearch(DAYS_INDEX, dayValue);
		return true;
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		return queryForRecord(context);
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.1f;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

}
