package gt.high5.database.model;

import java.util.Comparator;

import android.content.Context;

public class Total extends Table {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "")
	private String name = "";// package
	@TableAnnotation(defaultValue = "-1", increaseWhenUpdate = true)
	private int count = -1;
	@TableAnnotation(defaultValue = "1", isTransient = true)
	private float possibility = 1;

	private static String creator = null;
	private static Comparator<Table> comparator = new Comparator<Table>() {

		@Override
		public int compare(Table arg0, Table arg1) {
			// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		if (null == creator) {
			creator = buildCreator(this.getClass());
		}
		return creator;
	}

	public static Comparator<Table> getComparator() {
		return comparator;
	}

	public static void setComparator(Comparator<Table> comparator) {
		Total.comparator = comparator;
	}

	@Override
	public void currentQueryStatus(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initDefault(Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public String C() {
		String sql = null;
		try {
			sql = super.C(this);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String R() {
		String sql = null;
		try {
			sql = super.R(this);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String U(Table select) {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			sql = super.U(select, this);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String D() {
		String sql = null;
		try {
			sql = super.D(this);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String increase() {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			sql = super.increase(this);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public Table clone() {
		// TODO Auto-generated method stub
		Table result = null;
		try {
			result = super.clone(this);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
		++count;
	}

	@Override
	public void setPid(int pid) {
		// TODO Auto-generated method stub

	}

	public float getPossibility() {
		return possibility;
	}

	public void setPossibility(int count) {
		this.possibility *= count / this.count;
	}

}
