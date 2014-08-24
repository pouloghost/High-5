package gt.high5.chart.core;

import gt.high5.database.model.RecordTable;
import gt.high5.database.table.Total;
import android.content.Context;

public class FillContext {
	private int mIndex = -1;
	private Context mContext = null;
	private Total mTotal = null;
	private Class<? extends RecordTable> mRecord = null;
	private boolean mSuccess = false;

	public FillContext(int index, Context context, Total total,
			Class<? extends RecordTable> record) {
		setIndex(index);
		setContext(context);
		setRecord(record);
		setTotal(total);
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	public Total getTotal() {
		return mTotal;
	}

	public void setTotal(Total mTotal) {
		this.mTotal = mTotal;
	}

	public Class<? extends RecordTable> getRecord() {
		return mRecord;
	}

	public void setRecord(Class<? extends RecordTable> mRecord) {
		this.mRecord = mRecord;
	}

	public boolean isSuccess() {
		return mSuccess;
	}

	public void setSuccess(boolean mSuccess) {
		this.mSuccess = mSuccess;
	}

}
