package gt.high5.database.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.support.v4.util.ArrayMap;

public class ClassUtils {
	/**
	 * implementation using reflection supporting all abstract method with the
	 * same name
	 * 
	 * @param instance
	 * @param base
	 *            first parent class that should not be cloned in extend
	 *            hierarchy
	 * @return a clone of table
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T clone(T instance, Class<T> base)
			throws InstantiationException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		Class<? extends T> clazz = (Class<? extends T>) instance.getClass();
		T result = (T) clazz.newInstance();
		// Field[] fields = clazz.getDeclaredFields();
		Field[] fields = getAllFields(clazz, base);
		for (Field field : fields) {
			field.setAccessible(true);
			field.set(result, field.get(instance));
		}
		return result;
	}

	public static Object getDefaultValue(Field field) {
		TableAnnotation annotation = field.getAnnotation(TableAnnotation.class);
		if (null != annotation) {
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
		} else {
			return null;
		}
	}

	public static <T> Object getValue(T table, Field field)
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

	public static <T> void setValue(T table, Field field, Object value)
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

	/**
	 * get fields from clazz and all its super classes
	 * 
	 * @param clazz
	 * @param base
	 *            first parent class that should not be reflected in extend
	 *            hierarchy
	 * @return all fields
	 */
	@SuppressWarnings("unchecked")
	public static <T> Field[] getAllFields(Class<? extends T> clazz,
			Class<T> base) {
		ArrayMap<String, Field> name2Field = new ArrayMap<String, Field>();
		while (clazz != base) {
			Field[] fields = clazz.getDeclaredFields();
			String name = null;
			for (Field field : fields) {
				name = field.getName();
				if (!name2Field.containsKey(name)) {
					name2Field.put(name, field);
				}
			}

			clazz = (Class<? extends T>) clazz.getSuperclass();
		}

		Field[] fields = new Field[name2Field.size()];
		fields = name2Field.values().toArray(fields);

		return fields;
	}

	@SuppressWarnings("unchecked")
	public static <T> Method methodForName(Class<? extends T> clazz,
			Class<T> base, String name, Class<?>... parameterTypes) {
		Method result = null;
		while (null == result && clazz != base) {
			try {
				result = clazz.getDeclaredMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				clazz = (Class<? extends T>) clazz.getSuperclass();
			}
		}
		return result;
	}
}
