package gt.high5.database.accessor;

import gt.high5.database.model.ClassUtils;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
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

	private static final String BACKUP_PATH = "high5";

	private SQLiteDatabase mDatabase = null;
	private TableParser mTableParser = null;
	private DatabaseManager mManager = null;

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
				// null if not enough params in
				reference = new SoftReference<DatabaseAccessor>(null);
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
		mManager = new DatabaseManager(context, parser);
		mDatabase = mManager.getWritableDatabase();
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
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			ArrayList<Table> result = new ArrayList<Table>();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				Class<? extends Table> clazz = table.getClass();
				do {
					Table data = clazz.newInstance();
					Field[] fields = ClassUtils
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
		} finally {
			if (null != cursor) {
				cursor.close();
			}
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

	// ----------------------sql command interface-------------------
	public boolean excute(String sql) {
		try {
			mDatabase.execSQL(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * raw query remember to close cursor
	 * 
	 * @param sql
	 * @return
	 */
	public Cursor query(String sql) {
		try {
			return mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			return null;
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

	/**
	 * backup database associated with this accessor
	 * 
	 * @throws Exception
	 *             when backup goes wrong
	 */
	@SuppressWarnings("resource")
	public void backup() throws Exception {
		try {
			if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
					.getExternalStorageState())) {
				// lock db
				mDatabase.close();
				// source db
				String srcPath = mDatabase.getPath();
				File srcFile = new File(srcPath);
				// destination file
				File dstFolder = new File(
						Environment.getExternalStorageDirectory(), BACKUP_PATH);
				if (!dstFolder.exists()) {
					dstFolder.mkdir();
				}
				File dstFile = new File(dstFolder, mTableParser.getFile());
				if (!dstFile.exists()) {
					dstFile.createNewFile();
				}
				// copy
				FileChannel src = new FileInputStream(srcFile).getChannel();
				FileChannel dst = new FileOutputStream(dstFile).getChannel();

				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
			} else {
				throw new Exception();
			}
		} finally {
			// reopen database
			mDatabase = mManager.getWritableDatabase();
		}
	}

	/**
	 * restore database associated with this accessor
	 * 
	 * @throws Exception
	 *             when restore goes wrong
	 */
	@SuppressWarnings("resource")
	public void restore() throws Exception {
		try {
			if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
					.getExternalStorageState())) {
				// lock db
				mDatabase.close();
				// data/data db
				String dstPath = mDatabase.getPath();
				File dstFile = new File(dstPath);
				// backup file
				File srcFolder = new File(
						Environment.getExternalStorageDirectory(), BACKUP_PATH);
				if (!srcFolder.exists()) {
					throw new Exception();
				}
				File srcFile = new File(srcFolder, mTableParser.getFile());
				if (!srcFile.exists()) {
					throw new Exception();
				}
				// copy
				FileChannel src = new FileInputStream(srcFile).getChannel();
				FileChannel dst = new FileOutputStream(dstFile).getChannel();

				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
			} else {
				throw new Exception();
			}
		} finally {
			// reopen database
			mDatabase = mManager.getWritableDatabase();
		}
	}

	/**
	 * remove all data in database
	 */
	public void clean() {
		mDatabase.close();
		String path = mDatabase.getPath();
		File file = new File(path);
		file.delete();
		mDatabase = mManager.getWritableDatabase();
	}
}
