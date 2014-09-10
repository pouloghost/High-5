package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.Table;

import java.util.HashMap;

import android.database.Cursor;

public class RawRecord {
	public static String TYPE_DAY_OF_MONTH = "DayOfMonth";
	public static String TYPE_LAST_PACKAGE = "LastPackage";
	public static String TYPE_NETWORK = "Network";
	public static String TYPE_RING_MODE = "RingMode";
	public static String TYPE_RING_VOLUMN = "RingVolumn";
	public static String TYPE_TIME = "Time";
	public static String TYPE_TOTAL = "Total";
	public static String TYPE_WEEK_DAY = "WeekDay";
	public static String TYPE_WIFI_NAME = "WifiName";

	interface RecordOperation {
		public Object queryForRecord(RecordContext context);

		public Object queryForRead(RecordContext context);

		public Class<?> getType();
	}

	private static HashMap<String, RecordOperation> recordOperations = new HashMap<String, RawRecord.RecordOperation>();
	static {
	}

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
	}

	public Object getValue(String key) {
		return mValues.get(key);
	}

	public String getCreator() {
		StringBuilder sql = new StringBuilder(
				"CREATE TABLE IF NOT EXISTS RawRecord (id INTEGER PRIMARY KEY AUTOINCREMENT");
		for (String key : recordOperations.keySet()) {
			sql.append(", " + key + " " + recordOperations.get(key).getType());
		}
		sql.append(")");
		return sql.toString();
	}

	public String C() {
		boolean hasValue = false;
		StringBuilder sql = new StringBuilder("INSERT INTO RawRecord");
		StringBuilder cols = new StringBuilder(" (");
		StringBuilder vals = new StringBuilder(" VALUES(");
		for (String key : recordOperations.keySet()) {
			if (hasValue) {
				cols.append(" ,");
				vals.append(" ,");
			}

			Object value = mValues.get(key);
			if (null != value) {
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

	@SuppressWarnings("unchecked")
	public RawRecord clone() {
		RawRecord clone = new RawRecord();
		clone.mValues = (HashMap<String, Object>) this.mValues.clone();
		return clone;
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
