package gt.high5.database.model;

import android.content.Context;

public abstract class Table {

	/*
	 * all sub classes that is not total, must have a setter named setPid
	 * indicating the reference to package id in total table
	 * 
	 * all sub classes must have a default constructor
	 */

	/*
	 * accessors implemented using static method in Table
	 */
	public abstract String getCreator();

	public abstract String C();

	public abstract String R();

	public abstract String U(Table select);

	public abstract String D();

	public abstract String increase();

	/*
	 * for new instances
	 */
	public abstract Table clone();

	public abstract void initDefault(Context context);// all field should be
														// inited except pid

	public abstract void record(Context context);

	/*
	 * id field and pid field
	 */
	public abstract int getId();

	public abstract void setId(int id);

	public abstract void setPid(int pid);

	public abstract int getCount();

	public abstract void currentQueryStatus(Context context);

}
