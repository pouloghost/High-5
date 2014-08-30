package gt.high5.database.model;

import gt.high5.core.service.RecordContext;
import android.content.Context;

/**
 * @author GT
 * 
 *         record table for a static record
 * 
 *         all sub classes must have a default constructor
 */
public abstract class RecordTable extends Table {

	/**
	 * for query table using current status
	 * 
	 * @param context
	 * 
	 * @return whether init is successful
	 */
	public abstract boolean queryForRecord(RecordContext context);

	public abstract boolean queryForRead(RecordContext context);

	public abstract int getCount();

	/**
	 * return a default possibility when there is no existing result
	 * 
	 * @param context
	 * @return
	 */
	public abstract float getDefaultPossibility(Context context);

	public abstract String increase();

	public abstract void increaseCount(int add);

	/**
	 * initializing default data for a newly created table
	 * 
	 * all field should be inited except pid
	 * 
	 * @param context
	 * 
	 * @return whether init is successful
	 */
	public abstract boolean initDefault(RecordContext context);

	/**
	 * recording table must have a pid for package id
	 * 
	 * except for Total
	 * 
	 * @param pid
	 * 
	 */
	public abstract void setPid(int pid);

}
