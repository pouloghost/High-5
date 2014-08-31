package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

import java.util.Calendar;

import android.content.Context;

public class DayOfMonth extends SimpleRecordTable {

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
		day = calendar.get(Calendar.DAY_OF_MONTH);
		setPid(context.getTotal().getId());
		return true;
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		return queryForRecord(context);
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.03f;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

}
