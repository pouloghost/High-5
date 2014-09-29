package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.raw.RingVolumnRecordOperation;

public class RingVolumn extends AbstractVolumn {

	private static RingVolumnRecordOperation recordOperation = new RingVolumnRecordOperation();

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return queryForRecord(context, rawRecord);
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		setPid(context.getTotal().getId());
		Integer value = (Integer) rawRecord
				.getValue(RawRecord.TYPE_RING_VOLUMN);
		return checkAndSetPercent(value);
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		setPid(context.getTotal().getId());
		return checkAndSetPercent((Integer) recordOperation
				.queryForRecord(context));
	}

	@Override
	public Object getValue() {
		return getPercent();
	}

	private boolean checkAndSetPercent(Integer value) {
		if (null != value) {
			setPercent(value);
			return true;
		}
		return false;
	}

}
