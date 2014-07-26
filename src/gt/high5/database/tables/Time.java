package gt.high5.database.tables;

import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import gt.high5.widget.WidgetProvider;

import java.util.Calendar;

import android.content.Context;

/**
 * @author GT
 * 
 *         time region statics
 */
public class Time extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int region = -1;

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	@Override
	public void currentQueryStatus(Context context) {
		Calendar calendar = Calendar.getInstance();
		int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60
				+ calendar.get(Calendar.MINUTE);
		int devition = (WidgetProvider.UPDATE_INTERVAL / 1000 / 60);
		region = minutes / devition;
	}
}
