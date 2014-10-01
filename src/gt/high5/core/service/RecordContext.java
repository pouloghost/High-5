package gt.high5.core.service;

import gt.high5.database.model.RecordTable;
import gt.high5.database.table.Total;
import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         wrapper for context needed when record
 */
public class RecordContext {
	private Context context = null;
	private Total total = null;
	private int state = RecordTable.READ_DONE;

	public RecordContext(Context context, Total total) {
		setContext(context);
		setTotal(total);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Total getTotal() {
		return total;
	}

	public void setTotal(Total total) {
		this.total = total;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void recordNext() {
		++this.state;
	}
}
