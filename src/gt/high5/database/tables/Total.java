package gt.high5.database.tables;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.ClassUtils;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.model.TableUtils;

import java.util.Comparator;

import android.content.Context;

/**
 * @author GT
 * 
 *         running statics for a certain package
 */
/**
 * @author GT
 * 
 */
public class Total extends RecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "")
	private String name = "";// package
	@TableAnnotation(defaultValue = "-1", increaseWhenUpdate = true)
	private int count = -1;
	@TableAnnotation(defaultValue = "1", isTransient = true)
	private float possibility = 1;

	/**
	 * comparator for sorting total to get high 5
	 */
	private static Comparator<Table> comparator = new Comparator<Table>() {

		@Override
		public int compare(Table arg0, Table arg1) {
			float p0 = ((Total) arg0).getPossibility();
			float p1 = ((Total) arg1).getPossibility();
			return p1 > p0 ? 1 : p1 == p0 ? 0 : -1;
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
	public boolean currentQueryStatus(RecordContext context) {
		return true;
	}

	@Override
	public boolean initDefault(RecordContext context) {
		count = 0;
		return true;
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
			result = (RecordTable) ClassUtils.clone(this, Table.class);
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
	public void increaseCount(int add) {
		count += add;
	}

	@Override
	public void setPid(int pid) {
		this.id = pid;
	}

	public float getPossibility() {
		return possibility;
	}

	/**
	 * set P(Status|CurrentApp) or P(Current)
	 * 
	 * naive bayes
	 * 
	 * @param count
	 *            other status count
	 * 
	 * @param isAll
	 *            is setting P(Current)
	 */
	public void setPossibility(int count, boolean isAll) {
		if (isAll) {
			this.possibility *= (float) this.count / count;
		} else {
			this.possibility *= (float) count / this.count;
		}
	}

	public void setPossibility(float possibility) {
		this.possibility *= possibility;
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.01f;
	}

}
