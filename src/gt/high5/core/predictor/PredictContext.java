package gt.high5.core.predictor;

import android.content.Context;

public class PredictContext {
	private Context mContext = null;

	public PredictContext(Context context) {
		mContext = context;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

}
