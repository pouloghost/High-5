package gt.high5.database.model;

import gt.high5.widget.WidgetProvider;

import java.util.Calendar;

import android.content.Context;

public class Time extends Table {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "-1")
	private int pid = -1;
	@TableAnnotation(defaultValue = "-1")
	private int region = 0;
	@TableAnnotation(defaultValue = "-1", increaseWhenUpdate = true)
	private int count = -1;

	private static String creator = null;

	public int getPid() {
		return pid;
	}

	@Override
	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
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
		updateRegion();
		count = 1;
	}

	@Override
	public void record(Context context) {
		// TODO Auto-generated method stub
		updateRegion();
		++count;
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
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	public void updateRegion() {
		Calendar calendar = Calendar.getInstance();
		int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60
				+ calendar.get(Calendar.MINUTE);
		region = minutes / (WidgetProvider.RECORD_INTERVAL / 1000 / 60);
	}
}
