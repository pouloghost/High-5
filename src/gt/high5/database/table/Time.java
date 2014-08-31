package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

import java.util.Calendar;

import android.content.Context;

/**
 * @author GT
 * 
 *         time region statics
 */
public class Time extends SimpleRecordTable {

	// time region length in minutes
	private static int regionLength = 15;

	@TableAnnotation(defaultValue = "-1")
	private int region = -1;

	public static void setRegionLength(int length) {
		Time.regionLength = length;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	@Override
	public boolean queryForRecord(RecordContext context) {
		Calendar calendar = Calendar.getInstance();
		int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60
				+ calendar.get(Calendar.MINUTE);
		region = minutes / regionLength;
		setPid(context.getTotal().getId());
		return true;
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		return queryForRecord(context);
	}

	@Override
	public boolean initDefault(RecordContext context) {
		count = 1;
		return queryForRecord(context);
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.0125f;
	}
}
