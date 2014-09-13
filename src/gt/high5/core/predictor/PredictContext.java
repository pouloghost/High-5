package gt.high5.core.predictor;

import gt.high5.database.table.Total;
import android.content.Context;

public class PredictContext {
	private Context mContext = null;
	private Total mTotal = null;

	public PredictContext(Context context) {
		this(context, null);
	}

	public PredictContext(Context context, Total total) {
		mContext = context;
		setTotal(total);
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

}
