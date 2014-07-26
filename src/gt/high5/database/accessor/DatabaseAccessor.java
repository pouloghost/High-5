package gt.high5.database.accessor;

import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableUtils;

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

/**
 * @author GT
 * 
 *         database modifier interface
 * 
 *         all sql command are provided by an object representing the table
 * 
 *         this is only a wrapper for real DB operation
 */
public class DatabaseAccessor {
	private SQLiteDatabase mDatabase = null;
	private TableParser mTableParser = null;

	/**
	 * cached accessor for each xml file
	 * 
	 * xml.id -> accessor
	 */
	private static SparseArray<SoftReference<DatabaseAccessor>> accessorCache = new SparseArray<SoftReference<DatabaseAccessor>>();

	/**
	 * for the caller need a TableParser for other usage
	 * 
	 * @param context
	 * @param parser
	 * @param id
	 *            R.xml.id
	 * @return the DatabaseAccessor associated with id
	 */
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

	/**
	 * automatically create a TableParser
	 * 
	 * @param context
	 * @param id
	 * @return the DatabaseAccessor associated with id
	 */
	public static DatabaseAccessor getAccessor(Context context, int id) {
		TableParser parser = null;
		try {
			parser = new TableParser(context.getResources().getXml(id));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getAccessor(context, parser, id);
	}

	/**
	 * singleton constructor
	 * 
	 * @param context
	 * @param parser
	 */
	private DatabaseAccessor(Context context, TableParser parser) {
		mTableParser = parser;
		DatabaseManager manager = new DatabaseManager(context, parser);
		mDatabase = manager.getWritableDatabase();
	}

	/**
	 * @return record tables defined in xml
	 */
	public List<Class<? extends RecordTable>> getTables() {
		return mTableParser.getTables();
	}

	// ---------------------CRUD--------------------------
	public boolean C(Table table) {
		String sql = table.C();
		if (null == sql) {
			return false;
		}
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
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
					Field[] fields = TableUtils
							.getAllFields(clazz, Table.class);
					for (Field field : fields) {
						if (TableUtils.shouldIgnoreField(field, false)) {
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
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * increase count of the table
	 * 
	 * @param table
	 * @return whether update works
	 */
	public boolean increase(RecordTable table) {
		String sql = table.increase();
		if (null == sql) {
			return false;
		}
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean exists(Table table) {
		ArrayList<Table> query = R(table);
		return null == query || 0 == query.size();
	}
}
