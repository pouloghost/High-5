package gt.high5.database.model;

import gt.high5.activity.MainActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import android.support.v4.util.ArrayMap;
import android.util.Log;

public class TableUtils {

	private static boolean isDebugging = true;

	private static HashMap<Class<?>, String> typeMap = null;
	static {
		typeMap = new HashMap<Class<?>, String>();
		typeMap.put(Integer.class, "INTEGER");
		typeMap.put(Double.class, "DOUBLE");
		typeMap.put(String.class, "TEXT");
		typeMap.put(int.class, "INTEGER");
		typeMap.put(double.class, "DOUBLE");
	}

	public static String C(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		// Field[] fields = clazz.getDeclaredFields();
		Field[] fields = getAllFields(clazz);
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("INSERT INTO "
				+ clazz.getSimpleName());
		StringBuilder cols = new StringBuilder(" (");
		StringBuilder vals = new StringBuilder(" VALUES(");
		for (Field field : fields) {
			if (shouldIgnoreField(field, true)) {
				continue;
			}

			if (hasValue) {
				cols.append(" ,");
				vals.append(" ,");
			}

			field.setAccessible(true);
			cols.append(field.getName());
			vals.append(getFieldValueString(field, field.get(table)));

			hasValue = true;
		}
		if (!hasValue) {
			return null;
		}

		cols.append(") ");
		vals.append(") ");
		sql.append(cols);
		sql.append(vals);

		String sqlString = sql.toString();
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "create "
					+ table.getClass().getSimpleName() + " " + sqlString);
		}
		return sqlString;
	}

	public static String D(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		String where = getWhereClause(table);
		if (null == where) {
			return null;
		}

		String sqlString = "DELETE FROM " + clazz.getSimpleName() + where;
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "delete "
					+ table.getClass().getSimpleName() + " " + sqlString);
		}
		return sqlString;
	}

	public static String U(Table select, Table table)
			throws IllegalAccessException, IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		String where = getWhereClause(select);
		if (null == where) {
			return null;
		}

		// Field[] fields = clazz.getDeclaredFields();
		Field[] fields = getAllFields(clazz);
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("UPDATE " + clazz.getSimpleName()
				+ " SET ");

		for (Field field : fields) {
			if (shouldIgnoreField(field, true)) {
				continue;
			}

			Object def = getDefaultValue(field);
			Object val = getValue(table, field);
			if (!def.equals(val)) {
				if (hasValue) {
					sql.append(", ");
				}
				hasValue = true;
				sql.append(field.getName() + " = "
						+ getFieldValueString(field, val));
			}
		}
		if (!hasValue) {
			return null;
		}

		sql.append(where);

		String sqlString = sql.toString();
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "update "
					+ table.getClass().getSimpleName() + " " + sqlString);
		}
		return sqlString;
	}

	public static String R(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		String where = getWhereClause(table);
		if (null == where) {
			where = "";
		}

		String sqlString = "SELECT * FROM " + clazz.getSimpleName() + where;
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "read "
					+ table.getClass().getSimpleName() + " " + sqlString);
		}
		return sqlString;
	}

	public static String increase(Table table) throws IllegalAccessException,
			IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		// Field[] fields = clazz.getDeclaredFields();
		Field[] fields = getAllFields(clazz);
		boolean sqlAdded = false;
		boolean whereAdded = false;
		StringBuilder sql = new StringBuilder("UPDATE " + clazz.getSimpleName()
				+ " SET ");
		StringBuilder where = new StringBuilder(" WHERE id = " + table.getId());

		for (Field field : fields) {
			if (shouldIgnoreField(field, true)) {
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
					where.append(field.getName() + " = "
							+ getFieldValueString(field, val));
					whereAdded = true;
				}
			}
		}
		if (!sqlAdded) {
			return null;
		}

		sql.append(where);
		String sqlString = sql.toString();
		{
			Log.d(MainActivity.LOG_TAG, "increase "
					+ table.getClass().getSimpleName() + " " + sqlString);
		}
		return sqlString;
	}

	/*
	 * implementation using reflection supporting all abstract method with the
	 * same name
	 */
	public static Table clone(Table table) throws InstantiationException,
			IllegalAccessException {
		Class<? extends Table> clazz = table.getClass();
		Table result = (Table) clazz.newInstance();
		// Field[] fields = clazz.getDeclaredFields();
		Field[] fields = getAllFields(clazz);
		for (Field field : fields) {
			field.setAccessible(true);
			field.set(result, field.get(table));
		}
		return result;
	}

	public static String buildCreator(Class<? extends Table> clazz) {
		StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS "
				+ clazz.getSimpleName()
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT");
		// Field[] fields = clazz.getDeclaredFields();
		Field[] fields = getAllFields(clazz);
		for (Field field : fields) {
			if (shouldIgnoreField(field, true)) {
				continue;
			}
			sql.append(", " + field.getName() + " "
					+ typeMap.get(field.getType()));
		}
		sql.append(")");

		String sqlString = sql.toString();
		if (isDebugging || MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "creator " + clazz.getSimpleName()
					+ " " + sqlString);
		}
		return sqlString;
	}

	private static String getWhereClause(Table table)
			throws IllegalAccessException, IllegalArgumentException {
		Class<? extends Table> clazz = table.getClass();
		// Field[] fields = clazz.getDeclaredFields();
		Field[] fields = getAllFields(clazz);
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder(" WHERE ");

		for (Field field : fields) {
			if (shouldIgnoreField(field, false)) {
				continue;
			}

			Object def = getDefaultValue(field);
			Object val = getValue(table, field);
			if (!def.equals(val)) {
				if (hasValue) {
					sql.append(" AND ");
				}
				hasValue = true;
				sql.append(field.getName() + " = "
						+ getFieldValueString(field, val));
			}
		}
		if (!hasValue) {
			return null;
		}

		String sqlString = sql.toString();
		// Log.d(MainActivity.GT_TAG, "where " +
		// table.getClass().getSimpleName()
		// + " " + sqlString);
		return sqlString;
	}

	public static Object getDefaultValue(Field field) {
		TableAnnotation annotation = field.getAnnotation(TableAnnotation.class);
		Class<?> clazz = field.getType();
		Object result = new Object();
		if (int.class == clazz || Integer.class == clazz) {
			result = Integer.valueOf(annotation.defaultValue());
		} else if (String.class == clazz) {
			result = annotation.defaultValue();
		} else if (double.class == clazz || Double.class == clazz) {
			result = Double.valueOf(annotation.defaultValue());
		}
		return result;
	}

	public static Object getValue(Table table, Field field)
			throws IllegalAccessException, IllegalArgumentException {
		Class<?> clazz = field.getType();
		field.setAccessible(true);
		Object result = new Object();
		if (int.class == clazz) {
			result = Integer.valueOf(field.getInt(table));
		} else if (Integer.class == clazz) {
			result = field.get(table);
		} else if (String.class == clazz) {
			result = field.get(table);
		} else if (double.class == clazz) {
			result = Double.valueOf(field.getDouble(table));
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
			if (int.class == step.getClass()
					|| Integer.class == step.getClass()) {
				Integer s = (Integer) step;
				if (s > 0) {
					stepString = "+" + s.intValue();
				} else {
					stepString = "" + s.intValue();
				}
			} else if (double.class == step.getClass()
					|| Double.class == step.getClass()) {
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

	/*
	 * should ignore field using general filter
	 */
	public static boolean shouldIgnoreField(Field field, boolean forceIgnoreId) {
		if (forceIgnoreId && "id".equalsIgnoreCase(field.getName())) {
			return true;
		}
		if (Modifier.isStatic(field.getModifiers())) {
			return true;
		}
		TableAnnotation annotation = field.getAnnotation(TableAnnotation.class);
		if (annotation.isTransient()) {
			return true;
		}
		return false;
	}

	private static String getFieldValueString(Field field, Object val) {
		String valueString = null;
		if (field.getType() == String.class) {
			valueString = "'" + val + "'";
		} else {
			valueString = "" + val;
		}
		return valueString;
	}

	@SuppressWarnings("unchecked")
	public static Field[] getAllFields(Class<? extends Table> clazz) {
		ArrayMap<String, Field> name2Field = new ArrayMap<String, Field>();
		while (clazz != Table.class) {
			Field[] fields = clazz.getDeclaredFields();
			String name = null;
			for (Field field : fields) {
				name = field.getName();
				if (!name2Field.containsKey(name)) {
					name2Field.put(name, field);
				}
			}

			clazz = (Class<? extends Table>) clazz.getSuperclass();
		}

		Field[] fields = new Field[name2Field.size()];
		fields = name2Field.values().toArray(fields);

		return fields;
	}
}