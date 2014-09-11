package gt.high5.database.raw;

import gt.high5.core.service.RecordContext;

/**
 * @author GT
 * 
 *         operation for recording a certain type of record
 */
public interface RecordOperation {
	public Object queryForRecord(RecordContext context);

	// public Object queryForRead(RecordContext context);

	public Class<?> getType();
}