package gt.high5.database.accessor;

import gt.high5.database.model.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAccessor {
	private SQLiteDatabase mDatabase = null;
	private TableParser mTableParser = null;

	public DatabaseAccessor(Context context, TableParser parser) {
		mTableParser = parser;
		DatabaseManager manager = new DatabaseManager(context, parser);
		mDatabase = manager.getWritableDatabase();
	}

	public List<Class<? extends Table>> getTables() {
		return mTableParser.getTables();
	}

	public boolean C(Table table) {
		String sql = table.C();
		if (null == sql) {
			return false;
		}
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			return false;
		}
	}

	public ArrayList<Table> R(Table table) {
		String sql = table.R();
		if (null == sql) {
			return null;
		}
		try {
			Cursor cursor = mDatabase.rawQuery(sql, null);
			ArrayList<Table> result = new ArrayList<Table>();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				Class<? extends Table> clazz = table.getClass();
				do {
					Table data = clazz.newInstance();
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						Class<?> fClass = field.getType();
						field.setAccessible(true);
						if (int.class == fClass || Integer.class == fClass) {
							int value = cursor.getInt(cursor
									.getColumnIndex(field.getName()));
							field.set(table, value);
						} else if (String.class == fClass) {
							String value = cursor.getString(cursor
									.getColumnIndex(field.getName()));
							field.set(table, value);
						} else if (double.class == fClass
								|| Double.class == fClass) {
							double value = cursor.getDouble(cursor
									.getColumnIndex(field.getName()));
							field.set(table, value);
						}
					}
					result.add(data);
				} while (cursor.moveToNext());
				return result;
			} else {// no data cursor.size == 0
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			return null;
		}
	}

	public boolean U(Table table) {
		String sql = table.U();
		if (null == sql) {
			return false;
		}
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			return false;
		}
	}

	public boolean D(Table table) {
		String sql = table.D();
		if (null == sql) {
			return false;
		}
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			return false;
		}
	}
}
