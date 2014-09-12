package gt.high5.database.table.nb;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.ClassUtils;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.model.TableUtils;
import gt.high5.database.raw.RawRecord;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author GT
 * 
 *         running statics for a certain package
 */
public class Total extends RecordTable implements Parcelable {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "")
	private String name = "";// package
	@TableAnnotation(defaultValue = DEFAULT_COUNT_STRING, increaseWhenUpdate = true)
	private int count = DEFAULT_COUNT_INT;
	@TableAnnotation(defaultValue = "-1")
	private long timestamp = -1;
	@TableAnnotation(defaultValue = "1", isTransient = true)
	private float possibility = 1;

	@Override
	public boolean initDefault(RecordContext context, RawRecord rawRecord) {
		count = 0;
		return true;
	}

	@Override
	public boolean queryForRecord(RecordContext context, RawRecord rawRecord) {
		this.name = context.getTotal().getName();
		return true;
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		return queryForRecord(context, null);
	}

	@Override
	public float getDefaultPossibility(RecordContext context) {
		return 0.01f;
	}

	@Override
	public String getCreator() {
		return TableUtils.buildCreator(this.getClass(), Table.class);
	}

	@Override
	public String C() {
		setTimestamp(System.currentTimeMillis());
		String sql = null;
		try {
			sql = TableUtils.C(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String R() {
		String sql = null;
		try {
			sql = TableUtils.R(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String U(Table select) {
		setTimestamp(System.currentTimeMillis());
		String sql = null;
		try {
			sql = TableUtils.U(select, this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String D() {
		String sql = null;
		try {
			sql = TableUtils.D(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String increase() {
		setTimestamp(System.currentTimeMillis());
		String sql = null;
		try {
			sql = TableUtils.increase(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public RecordTable clone() {
		RecordTable result = null;
		try {
			result = (RecordTable) ClassUtils.clone(this, Table.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void increaseCount(int add) {
		count += add;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void setPid(int pid) {
		this.id = pid;
	}

	public float getPossibility() {
		return possibility;
	}

	public void setPossibility(float possibility) {
		this.possibility = possibility;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public static Creator<Total> CREATOR = new Creator<Total>() {

		@Override
		public Total createFromParcel(Parcel source) {
			Total result = new Total();
			result.id = source.readInt();
			result.name = source.readString();
			result.count = source.readInt();
			result.possibility = source.readFloat();
			return result;
		}

		@Override
		public Total[] newArray(int size) {
			return new Total[size];
		}
	};

	@Override
	public int describeContents() {
		return 1;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(count);
		dest.writeFloat(possibility);
	}
}
