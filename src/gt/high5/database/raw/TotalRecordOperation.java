package gt.high5.database.raw;

import gt.high5.core.service.RecordContext;

public class TotalRecordOperation implements RecordOperation {
	@Override
	public Object queryForRecord(RecordContext context) {
		return context.getTotal().getName();
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

}
