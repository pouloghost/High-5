package gt.high5.core.service;

import gt.high5.database.tables.Total;
import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         wrapper for context needed when record
 */
public class RecordContext {
	private Context context = null;
	private RecordService service = null;
	private Total total = null;

	public RecordContext(Context context, RecordService service, Total total) {
		setContext(context);
		setRecordService(service);
		setTotal(total);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public RecordService getRecordService() {
		return service;
	}

	public void setRecordService(RecordService service) {
		this.service = service;
	}

	public Total getTotal() {
		return total;
	}

	public void setTotal(Total total) {
		this.total = total;
	}
}
