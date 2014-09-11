package gt.high5.database.table.nb;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

public abstract class AbstractVolumn extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int percent = -1;

	@Override
	public float getDefaultPossibility(RecordContext context) {
		return 0.4f / context.getTotal().getCount();
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
}
