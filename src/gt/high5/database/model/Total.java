package gt.high5.database.model;

import android.content.Context;

public class Total extends Table {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "")
	private String name = "";// package
	@TableAnnotation(defaultValue = "-1", increaseWhenUpdate = true)
	private int count = -1;

	private static String creator = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	@Override
	public void initDefault() {
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
	public String U() {
		String sql = null;
		try {
			sql = super.U(this);
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

	}

	@Override
	public void setPid(int pid) {
		// TODO Auto-generated method stub

	}

}
