package gt.high5.database.table;

import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import android.content.Context;

public abstract class AbstractVolumn extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int percent = -1;

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.2f;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
}
