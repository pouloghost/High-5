package gt.high5.database.model;

/*
 * representing a simple table with field pid id and count
 * */
public abstract class SimpleRecordTable extends RecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "-1")
	private int pid = -1;
	@TableAnnotation(defaultValue = DEFAULT_COUNT_STRING, increaseWhenUpdate = true)
	protected int count = DEFAULT_COUNT_INT;

	@Override
	public String getCreator() {
		return TableUtils.buildCreator(this.getClass(), Table.class);
	}

	@Override
	public String C() {
		String sql = null;
		try {
			sql = TableUtils.C(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String R() {
		String sql = null;
		try {
			sql = TableUtils.R(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String U(Table select) {
		String sql = null;
		try {
			sql = TableUtils.U(select, this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String D() {
		String sql = null;
		try {
			sql = TableUtils.D(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String increase() {
		String sql = null;
		try {
			sql = TableUtils.increase(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public void increaseCount(int add) {
		count += add;
	}

	@Override
	public RecordTable clone() {
		RecordTable result = null;
		try {
			result = (RecordTable) ClassUtils.clone(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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

	public int getPid() {
		return this.pid;
	}

	@Override
	public int getCount() {
		return count;
	}
}
