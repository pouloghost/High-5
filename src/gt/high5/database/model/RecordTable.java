package gt.high5.database.model;

import gt.high5.core.service.RecordContext;

/**
 * @author GT
 * 
 *         record table for a static record
 * 
 *         all sub classes must have a default constructor
 */
public abstract class RecordTable extends Table {
	/*
	 * accessors implemented using static method in TableUtils
	 */

	public abstract String increase();

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

	public abstract int getCount();

	/**
	 * for query table using current status
	 * 
	 * @param context
	 * 
	 * @return whether init is successful
	 */
	public abstract boolean currentQueryStatus(RecordContext context);

}
