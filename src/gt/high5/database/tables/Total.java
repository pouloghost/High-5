package gt.high5.database.tables;

import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.model.TableUtils;

import java.util.Comparator;

import android.content.Context;

public class Total extends RecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "")
	private String name = "";// package
	@TableAnnotation(defaultValue = "-1", increaseWhenUpdate = true)
	private int count = -1;
	@TableAnnotation(defaultValue = "1", isTransient = true)
	private float possibility = 1;

	private static Comparator<Table> comparator = new Comparator<Table>() {

		@Override
		public int compare(Table arg0, Table arg1) {
			return (int) (((Total) arg0).getPossibility() - ((Total) arg1)
					.getPossibility());
		}
	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String getCreator() {
		return TableUtils.buildCreator(this.getClass(), Table.class);
	}

	public static Comparator<Table> getComparator() {
		return comparator;
	}

	public static void setComparator(Comparator<Table> comparator) {
		Total.comparator = comparator;
	}

	@Override
	public void currentQueryStatus(Context context) {

	}

	@Override
	public void initDefault(Context context) {

	}

	@Override
	public String C() {
		String sql = null;
		try {
			sql = TableUtils.C(this, Table.class);
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
			sql = TableUtils.R(this, Table.class);
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
			sql = TableUtils.U(select, this, Table.class);
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
			sql = TableUtils.D(this, Table.class);
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
	public RecordTable clone() {
		RecordTable result = null;
		try {
			result = (RecordTable) TableUtils.clone(this, Table.class);
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

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
	public void record(Context context) {
		++count;
	}

	@Override
	public void setPid(int pid) {

	}

	public float getPossibility() {
		return possibility;
	}

	public void setPossibility(int count) {
		this.possibility *= count / this.count;
	}

}
