package gt.high5.database.table.nb;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.raw.RingVolumnRecordOperation;

public class RingVolumn extends AbstractVolumn {

	private static RingVolumnRecordOperation recordOperation = new RingVolumnRecordOperation();
	@TableAnnotation(defaultValue = "-1")
	private int percent = -1;

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = rawRecord.getCount();
		return false;
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

	private boolean checkAndSetPercent(Integer value) {
		if (null != value) {
			setPercent(value);
			return true;
		}
		return false;
	}

}
