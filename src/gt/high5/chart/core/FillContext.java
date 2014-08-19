package gt.high5.chart.core;

import gt.high5.chart.core.DataFiller.CHART_TYPE;
import gt.high5.database.model.RecordTable;
import gt.high5.database.table.Total;
import android.content.Context;
import android.view.View;

public class FillContext {
	private CHART_TYPE mType = null;
	private View mView = null;
	private Context mContext = null;
	private Total mTotal = null;
	private Class<? extends RecordTable> mRecord = null;

	public FillContext(CHART_TYPE type, View view, Context context,
			Total total, Class<? extends RecordTable> record) {
		setType(type);
		setView(view);
		setContext(context);
		setRecord(record);
		setTotal(total);
	}

	public CHART_TYPE getType() {
		return mType;
	}

	public void setType(CHART_TYPE mType) {
		this.mType = mType;
	}

	public View getView() {
		return mView;
	}

	public void setView(View mView) {
		this.mView = mView;
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

}
