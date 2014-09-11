package gt.high5.database.raw;

import gt.high5.core.service.LogService;
import gt.high5.core.service.RecordContext;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableUtils;

import java.util.HashMap;

import android.database.Cursor;
import android.util.Log;

/**
 * @author GT
 * 
 *         raw record table
 */
public class RawRecord extends Table {
	// all available data types
	public static String TYPE_DAY_OF_MONTH = "DayOfMonth";
	public static String TYPE_LAST_PACKAGE = "LastPackage";
	public static String TYPE_NETWORK = "Network";
	public static String TYPE_RING_MODE = "RingMode";
	public static String TYPE_RING_VOLUMN = "RingVolumn";
	public static String TYPE_TIME = "Time";
	public static String TYPE_TOTAL = "Total";
	public static String TYPE_WEEK_DAY = "WeekDay";
	public static String TYPE_WIFI_NAME = "WifiName";
	/**
	 * mapping type name to operation
	 */
	private static HashMap<String, RecordOperation> recordOperations = new HashMap<String, RecordOperation>();
	static {
		recordOperations
				.put(TYPE_DAY_OF_MONTH, new DayOfMonthRecordOperation());

		recordOperations.put(TYPE_LAST_PACKAGE,
				new LastPackageRecordOperation());

		recordOperations.put(TYPE_NETWORK, new NetworkRecordOperation());

		recordOperations.put(TYPE_RING_MODE, new RingModeRecordOperation());

		recordOperations.put(TYPE_RING_VOLUMN, new RingVolumnRecordOperation());

		recordOperations.put(TYPE_TIME, new TimeRecordOperation());

		recordOperations.put(TYPE_TOTAL, new TotalRecordOperation());

		recordOperations.put(TYPE_WEEK_DAY, new WeekDayRecordOperation());

		recordOperations.put(TYPE_WIFI_NAME, new WifiNameRecordOperation());

	}

	private static boolean isDebugging = false;

	private int id = -1;
	private int count = 0;

	// store values in hashmap
	private HashMap<String, Object> mValues = new HashMap<String, Object>();

	public RawRecord() {

	}

	public RawRecord(Cursor cursor) {
		for (String key : recordOperations.keySet()) {
			int index = cursor.getColumnIndex(key);
			if (-1 != index) {
				if (Integer.class == recordOperations.get(key).getType()) {
					mValues.put(key, cursor.getInt(index));
				} else if (String.class == recordOperations.get(key).getType()) {
					mValues.put(key, cursor.getString(index));
				} else if (Double.class == recordOperations.get(key).getType()) {
					mValues.put(key, cursor.getDouble(index));
				} else if (Long.class == recordOperations.get(key).getType()) {
					mValues.put(key, cursor.getLong(index));
				}
			}
		}
		setId(cursor.getInt(cursor.getColumnIndex("id")));
		setCount(cursor.getInt(cursor.getColumnIndex("count")));
	}

	public Object getValue(String key) {
		return mValues.get(key);
	}

	public String getCreator() {
		StringBuilder sql = new StringBuilder(
				"CREATE TABLE IF NOT EXISTS RawRecord (id INTEGER PRIMARY KEY AUTOINCREMENT, count INTEGER");
		for (String key : recordOperations.keySet()) {
			sql.append(", "
					+ key
					+ " "
					+ TableUtils.getDataName(recordOperations.get(key)
							.getType()));
		}
		sql.append(")");
		Log.d(LogService.LOG_TAG, sql.toString());
		return sql.toString();
	}

	public String C() {
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("INSERT INTO RawRecord");
		StringBuilder cols = new StringBuilder(" (");
		StringBuilder vals = new StringBuilder(" VALUES(");
		for (String key : recordOperations.keySet()) {
			Object value = mValues.get(key);
			if (null != value) {
				if (hasValue) {
					cols.append(" ,");
					vals.append(" ,");
				}

				cols.append(key);
				vals.append(getValueString(mValues.get(key)));

				hasValue = true;
			}
		}

		if (!hasValue) {
			return null;
		}

		cols.append(") ");
		vals.append(") ");
		sql.append(cols);
		sql.append(vals);

		Log.d(LogService.LOG_TAG, sql.toString());
		return sql.toString();
	}

	public String R() {
		return "SELECT * FROM RawRecord";
	}

	public String U(Table select) {
		return null;
	}

	public String D() {
		return null;
	}

	public static String RCount() {
		return "SELECT COUNT(*) FROM RawRecord";
	}

	public static String D(int count) {
		return "DELETE FROM RawRecord WHERE id IN(SELECT id FROM RawRecord ORDER BY id LIMIT "
				+ count + " OFFSET 0)";
	}

	public void record(RecordContext context, int count) {
		for (String key : recordOperations.keySet()) {
			Object value = recordOperations.get(key).queryForRecord(context);
			if (null != value) {
				mValues.put(key, value);
			}
		}
		setCount(count);
	}

	@SuppressWarnings("unchecked")
	public RawRecord clone() {
		RawRecord clone = new RawRecord();
		clone.mValues = (HashMap<String, Object>) this.mValues.clone();
		return clone;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public static boolean isDebugging() {
		return isDebugging;
	}

	public static void setDebugging(boolean isDebugging) {
		RawRecord.isDebugging = isDebugging;
	}

	private String getValueString(Object value) {
		String valueString = null;
		if (value.getClass() == String.class) {
			valueString = "'" + value + "'";
		} else {
			valueString = "" + value;
		}
		return valueString;
	}
}
