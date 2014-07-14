package gt.high5.database.model;

import java.lang.reflect.Field;
import java.util.HashMap;

import android.content.Context;

public abstract class Table {
	private static HashMap<Class<?>, String> typeMap = null;
	static {
		typeMap = new HashMap<Class<?>, String>();
		typeMap.put(Integer.class, "INTEGER");
		typeMap.put(Double.class, "DOUBLE");
		typeMap.put(String.class, "TEXT");
		typeMap.put(int.class, "INTEGER");
		typeMap.put(double.class, "DOUBLE");
	}

	/*
	 * all sub classes that is not total, must have a setter named setPid
	 * indicating the reference to package id in total table
	 * 
	 * all sub classes must have a default constructor
	 */

	public abstract String getCreator();

	public abstract String C();

	public abstract String R();

	public abstract String U();

	public abstract String D();

	public abstract int getId();

	public abstract void setId(int id);

	public abstract void setPid(int pid);

	public abstract void record(Context context);

	public abstract void initDefault();// all field should be inited except pid
										// and id

	protected static String buildCreator(Class<? extends Table> clazz) {
		StringBuilder sql = new StringBuilder("CREATE TABLE "
				+ clazz.getSimpleName()
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if ("id".equalsIgnoreCase(field.getName())) {
				continue;
			}
			sql.append(", " + field.getName() + " "
					+ typeMap.get(field.getType()));
		}
		sql.append(")");

		return sql.toString();
	}

	protected static String C(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		Field[] fields = clazz.getDeclaredFields();
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("INSERT INTO "
				+ clazz.getSimpleName());
		StringBuilder cols = new StringBuilder(" (");
		StringBuilder vals = new StringBuilder(" VALUES(");
		for (Field field : fields) {
			if ("id".equalsIgnoreCase(field.getName())) {
				continue;
			}
			hasValue = true;
			cols.append(" ," + field.getName());
			field.setAccessible(true);
			vals.append(" ," + field.get(table));
		}
		if (!hasValue) {
			return null;
		}

		cols.append(") ");
		vals.append(") ");
		sql.append(cols);
		sql.append(vals);

		return sql.toString();
	}

	protected static String D(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		Field[] fields = clazz.getDeclaredFields();
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("DELETE FROM "
				+ clazz.getSimpleName() + " WHERE ");

		for (Field field : fields) {
			Object def = getDefaultValue(field);
			Object val = getValue(table, field);
			if (!def.equals(val)) {
				if (hasValue) {
					sql.append(" AND ");
				}
				hasValue = true;
				sql.append(field.getName() + " = " + val);
			}
		}
		if (!hasValue) {
			return null;
		}

		return sql.toString();
	}

	protected static String U(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		Field[] fields = clazz.getDeclaredFields();
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("UPDATE " + clazz.getSimpleName()
				+ " SET ");

		for (Field field : fields) {
			if ("id".equalsIgnoreCase(field.getName())) {
				continue;
			}
			Object def = getDefaultValue(field);
			Object val = getValue(table, field);
			if (!def.equals(val)) {
				if (hasValue) {
					sql.append(", ");
				}
				hasValue = true;
				sql.append(field.getName() + " = " + val);
			}
		}
		if (!hasValue) {
			return null;
		}

		sql.append(" WHERE id = " + table.getId());
		return sql.toString();
	}

	protected static String increase(Table table)
			throws IllegalAccessException, IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		Field[] fields = clazz.getDeclaredFields();
		boolean sqlAdded = false;
		boolean whereAdded = false;
		StringBuilder sql = new StringBuilder("UPDATE " + clazz.getSimpleName()
				+ " SET ");
		StringBuilder where = new StringBuilder(" WHERE id = " + table.getId());

		for (Field field : fields) {
			if ("id".equalsIgnoreCase(field.getName())) {
				continue;
			}
			Object def = getDefaultValue(field);
			Object val = getValue(table, field);

			if (!def.equals(val)) {
				String step = getStep(table, field);
				if (null != step) {
					if (sqlAdded) {
						sql.append(", ");
					}
					sql.append(field.getName() + " = " + field.getName() + step);
					sqlAdded = true;
				} else {
					if (whereAdded) {
						where.append(" AND ");
					}
					where.append(field.getName() + " = " + val);
					whereAdded = true;
				}
			}
		}
		if (!sqlAdded) {
			return null;
		}

		sql.append(where);
		return sql.toString();
	}

	protected static String R(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		Field[] fields = clazz.getDeclaredFields();
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("SELECT * FROM "
				+ clazz.getSimpleName() + " WHERE ");

		for (Field field : fields) {
			Object def = getDefaultValue(field);
			Object val = getValue(table, field);
			if (!def.equals(val)) {
				if (hasValue) {
					sql.append(" AND ");
				}
				hasValue = true;
				sql.append(field.getName() + " = " + val);
			}
		}
		if (!hasValue) {
			return null;
		}

		return sql.toString();
	}

	public static Object getDefaultValue(Field field) {
		TableAnnotation annotation = field.getAnnotation(TableAnnotation.class);
		Class<?> clazz = field.getType();
		Object result = new Object();
		if (int.class == clazz || Integer.class == clazz) {
			result = new Integer(annotation.defaultValue());
		} else if (String.class == clazz) {
			result = annotation.defaultValue();
		} else if (double.class == clazz || Double.class == clazz) {
			result = new Double(annotation.defaultValue());
		}
		return result;
	}

	public static Object getValue(Table table, Field field)
			throws IllegalAccessException, IllegalArgumentException {
		Class<?> clazz = field.getType();
		field.setAccessible(true);
		Object result = new Object();
		if (int.class == clazz) {
			result = new Integer(field.getInt(table));
		} else if (Integer.class == clazz) {
			result = field.get(table);
		} else if (String.class == clazz) {
			result = field.get(table);
		} else if (double.class == clazz) {
			result = new Double(field.getDouble(table));
		} else if (Double.class == clazz) {
			result = field.get(table);
		}
		return result;
	}

	public static void setValue(Table table, Field field, Object value)
			throws IllegalAccessException, IllegalArgumentException {
		Class<?> clazz = field.getType();
		field.setAccessible(true);
		if (int.class == clazz) {
			field.set(table, ((Integer) value).intValue());
		} else if (Integer.class == clazz) {
			field.set(table, ((Integer) value));
		} else if (String.class == clazz) {
			field.set(table, ((String) value));
		} else if (double.class == clazz) {
			field.set(table, ((Double) value).doubleValue());
		} else if (Double.class == clazz) {
			field.set(table, ((Double) value));
		}
	}

	private static String getStep(Table table, Field field)
			throws IllegalAccessException, IllegalArgumentException {
		Class<?> clazz = field.getType();
		if (!(int.class == clazz || double.class == clazz
				|| Integer.class == clazz || Double.class == clazz)) {
			return null;
		}
		TableAnnotation annotation = field.getAnnotation(TableAnnotation.class);
		if (annotation.increaseWhenUpdate()) {
			Object step = getValue(table, field);
			String stepString = null;
			if (int.class == step.getClass()) {
				int s = (int) step;
				if (s > 0) {
					stepString = "+" + s;
				} else {
					stepString = "" + s;
				}
			} else if (Integer.class == step.getClass()) {
				Integer s = (Integer) step;
				if (s > 0) {
					stepString = "+" + s.intValue();
				} else {
					stepString = "" + s.intValue();
				}
			} else if (double.class == step.getClass()) {
				double s = (double) step;
				if (s > 0) {
					stepString = "+" + s;
				} else {
					stepString = "" + s;
				}
			} else if (Double.class == step.getClass()) {
				Double s = (Double) step;
				if (s > 0) {
					stepString = "+" + s.doubleValue();
				} else {
					stepString = "" + s.doubleValue();
				}
			}

			return stepString;
		} else {
			return null;
		}
	}
}
