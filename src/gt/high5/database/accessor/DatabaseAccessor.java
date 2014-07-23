package gt.high5.database.accessor;

import gt.high5.R;
import gt.high5.database.model.Table;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

public class DatabaseAccessor {
	private SQLiteDatabase mDatabase = null;
	private TableParser mTableParser = null;

	private static SparseArray<SoftReference<DatabaseAccessor>> accessorCache = new SparseArray<SoftReference<DatabaseAccessor>>();

	public static DatabaseAccessor getAccessor(Context context,
			TableParser parser, int id) {
		SoftReference<DatabaseAccessor> reference = accessorCache.get(id);
		DatabaseAccessor accessor = null;
		if (null == reference) {
			if (null != context && null != parser) {
				accessor = new DatabaseAccessor(context, parser);
				reference = new SoftReference<DatabaseAccessor>(accessor);
			} else {
				reference = new SoftReference<DatabaseAccessor>(null);// null if
																		// not
																		// enough
																		// params
																		// in
			}
		}
		accessor = reference.get();
		return accessor;
	}

	public static DatabaseAccessor getAccessor(Context context, int id) {
		TableParser parser = null;
		try {
			parser = new TableParser(context.getResources().getXml(id));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getAccessor(context, parser, id);
	}

	private DatabaseAccessor(Context context, TableParser parser) {
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
			e.printStackTrace();
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
						if (Table.shouldIgnoreField(field, false)) {
							continue;
						}
						Class<?> fClass = field.getType();
						field.setAccessible(true);
						if (int.class == fClass || Integer.class == fClass) {
							int value = cursor.getInt(cursor
									.getColumnIndex(field.getName()));
							field.set(data, value);
						} else if (String.class == fClass) {
							String value = cursor.getString(cursor
									.getColumnIndex(field.getName()));
							field.set(data, value);
						} else if (double.class == fClass
								|| Double.class == fClass) {
							double value = cursor.getDouble(cursor
									.getColumnIndex(field.getName()));
							field.set(data, value);
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
			e.printStackTrace();
			return null;
		}
	}

	public boolean U(Table select, Table table) {
		String sql = table.U(select);
		if (null == sql) {
			return false;
		}
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
			e.printStackTrace();
			return false;
		}
	}

	public boolean increase(Table table) {
		String sql = table.increase();
		if (null == sql) {
			return false;
		}
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
}
