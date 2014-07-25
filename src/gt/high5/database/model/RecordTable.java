package gt.high5.database.model;

import android.content.Context;

public abstract class RecordTable extends Table{

	/*
	 * all sub classes that is not total, must have a setter named setPid
	 * indicating the reference to package id in total table
	 * 
	 * all sub classes must have a default constructor
	 */

	/*
	 * accessors implemented using static method in TableUtils
	 */

	public abstract String increase();

	public abstract void initDefault(Context context);// all field should be
														// inited except pid

	public abstract void record(Context context);

	public abstract void setPid(int pid);

	public abstract int getCount();

	public abstract void currentQueryStatus(Context context);

}
