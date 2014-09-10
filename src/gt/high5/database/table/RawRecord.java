package gt.high5.database.table;

import gt.high5.core.provider.PackageProvider;
import gt.high5.core.service.RecordContext;
import gt.high5.database.model.Table;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * @author GT
 * 
 *         raw record table
 */
public class RawRecord {
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

	// time region length in minutes
	private static int regionLength = 15;

	public static void setRegionLength(int length) {
		regionLength = length;
	}

	/**
	 * @author GT
	 * 
	 *         operation for recording a certain type of record
	 */
	interface RecordOperation {
		public Object queryForRecord(RecordContext context);

		// public Object queryForRead(RecordContext context);

		public Class<?> getType();
	}

	/**
	 * mapping type name to operation
	 */
	private static HashMap<String, RecordOperation> recordOperations = new HashMap<String, RawRecord.RecordOperation>();
	static {
		recordOperations.put(TYPE_DAY_OF_MONTH, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			}

			// @Override
			// public Object queryForRead(RecordContext context) {
			// return queryForRecord(context);
			// }

			@Override
			public Class<?> getType() {
				return Integer.class;
			}
		});

		recordOperations.put(TYPE_LAST_PACKAGE, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				PackageProvider provider = null;
				List<String> order = null;
				if (null != (provider = PackageProvider
						.getPackageProvider(context.getContext()))
						&& null != (order = provider
								.getLastPackageOrder(context.getContext()))) {
					Total total = context.getTotal();
					int index = order.indexOf(total.getName());
					// exists and not last one
					if (-1 != index && order.size() - 1 != index) {
						return order.get(index + 1);
					}
				}
				return null;
			}

			// @Override
			// public Object queryForRead(RecordContext context) {
			// // TODO Auto-generated method stub
			// return null;
			// }

			@Override
			public Class<?> getType() {
				return String.class;
			}
		});

		recordOperations.put(TYPE_NETWORK, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				ConnectivityManager manager = (ConnectivityManager) context
						.getContext().getSystemService(
								Context.CONNECTIVITY_SERVICE);
				if (null != manager) {
					NetworkInfo info = manager.getActiveNetworkInfo();
					if (null == info || (!info.isConnectedOrConnecting())) {
						return "NONE";
					} else {
						return info.getTypeName() + "_" + info.getSubtypeName();
					}
				} else {
					return null;
				}
			}

			@Override
			public Class<?> getType() {
				return String.class;
			}
		});

		recordOperations.put(TYPE_RING_MODE, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				return ((AudioManager) context.getContext().getSystemService(
						Context.AUDIO_SERVICE)).getMode();
			}

			@Override
			public Class<?> getType() {
				return Integer.class;
			}
		});

		recordOperations.put(TYPE_RING_VOLUMN, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				AudioManager manager = (AudioManager) context.getContext()
						.getSystemService(Context.AUDIO_SERVICE);
				int type = AudioManager.STREAM_RING;
				double max = manager.getStreamMaxVolume(type);
				double current = manager.getStreamVolume(type);
				return (int) (current / max * 10);
			}

			@Override
			public Class<?> getType() {
				return Integer.class;
			}
		});

		recordOperations.put(TYPE_TIME, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				Calendar calendar = Calendar.getInstance();
				int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60
						+ calendar.get(Calendar.MINUTE);
				return (int) (minutes / regionLength);
			}

			@Override
			public Class<?> getType() {
				return Integer.class;
			}
		});

		recordOperations.put(TYPE_TOTAL, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				return context.getTotal().getName();
			}

			@Override
			public Class<?> getType() {
				return String.class;
			}
		});

		recordOperations.put(TYPE_WEEK_DAY, new RecordOperation() {
			// week day
			private final int[] DAYS_INDEX = { Calendar.SUNDAY,
					Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
					Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, };

			@Override
			public Object queryForRecord(RecordContext context) {
				Calendar calendar = Calendar.getInstance();
				int dayValue = calendar.get(Calendar.DAY_OF_WEEK);
				return Arrays.binarySearch(DAYS_INDEX, dayValue);
			}

			@Override
			public Class<?> getType() {
				return Integer.class;
			}
		});

		recordOperations.put(TYPE_WIFI_NAME, new RecordOperation() {

			@Override
			public Object queryForRecord(RecordContext context) {
				WifiManager manager = (WifiManager) context.getContext()
						.getSystemService(Context.WIFI_SERVICE);
				if (null != manager) {
					WifiInfo info = manager.getConnectionInfo();
					String bssid = info.getBSSID();
					if (null == bssid) {
						if (manager.isWifiEnabled()) {
							bssid = "ON";
						} else {
							bssid = "OFF";
						}
					}
					return bssid;
				} else {
					return null;
				}
			}

			@Override
			public Class<?> getType() {
				return String.class;
			}
		});
	}

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
