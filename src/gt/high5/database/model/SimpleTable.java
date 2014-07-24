package gt.high5.database.model;

import android.content.Context;
/*
 * representing a simple table with field pid id and count
 * */
public abstract class SimpleTable extends Table {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "-1")
	private int pid = -1;
	@TableAnnotation(defaultValue = "-1", increaseWhenUpdate = true)
	private int count = -1;

	@Override
	public String getCreator() {
		return TableUtils.buildCreator(this.getClass());
	}

	@Override
	public String C() {
		String sql = null;
		try {
			sql = TableUtils.C(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String R() {
		String sql = null;
		try {
			sql = TableUtils.R(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String U(Table select) {
		String sql = null;
		try {
			sql = TableUtils.U(select, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String D() {
		String sql = null;
		try {
			sql = TableUtils.D(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String increase() {
		String sql = null;
		try {
			sql = TableUtils.increase(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public Table clone() {
		Table result = null;
		try {
			result = TableUtils.clone(this);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void initDefault(Context context) {
		currentQueryStatus(context);
		count = 1;
	}

	@Override
	public void record(Context context) {
		++count;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getPid(){
		return this.pid;
	}
	@Override
	public int getCount() {
		return count;
	}
}