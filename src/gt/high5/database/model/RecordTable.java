package gt.high5.database.model;

import gt.high5.core.service.RecordContext;
import gt.high5.database.raw.RawRecord;

/**
 * @author GT
 * 
 *         record table for a static record
 * 
 *         all sub classes must have a default constructor
 */
/**
 * @author GT
 * 
 */
public abstract class RecordTable extends Table {
	// return value of queryForRead
	public static final int READ_FAILED = -1;
	public static final int READ_DONE = 0;
	public static final int READ_CONTINUE = 1;

	public static final int DEFAULT_COUNT_INT = -1;
	public static final String DEFAULT_COUNT_STRING = "-1";

	/**
	 * initializing default data for a newly created table
	 * 
	 * all field should be inited except pid
	 * 
	 * @param context
	 * 
	 * @return whether init is successful
	 */
	public abstract boolean initDefault(RecordContext context,
			RawRecord rawRecord);

	/**
	 * for query table using current status
	 * 
	 * @param context
	 * 
	 * @return whether init is successful
	 */
	public abstract boolean queryForRecord(RecordContext context,
			RawRecord rawRecord);

	public abstract int queryForRead(RecordContext context);

	/**
	 * return a default possibility when there is no existing result
	 * 
	 * @param context
	 * @return
	 */

	public abstract int getCount();

	public abstract String increase();

	public abstract void increaseCount(int add);

	/**
	 * recording table must have a pid for package id
	 * 
	 * except for Total
	 * 
	 * @param pid
	 * 
	 */
	public abstract void setPid(int pid);

	/**
	 * @return the value of data field, if more than one field, a map will be
	 *         returned
	 */
	public abstract Object getValue();

}
