package gt.high5.core.predictor;

import gt.high5.database.accessor.DatabaseAccessor;
import android.content.Context;

public class PredictContext {
	private DatabaseAccessor mAccessor = null;
	private Context mContext = null;

	public PredictContext(DatabaseAccessor accessor, Context context) {
		mAccessor = accessor;
		mContext = context;
	}

	public DatabaseAccessor getAccessor() {
		return mAccessor;
	}

	public void setAccessor(DatabaseAccessor mAccessor) {
		this.mAccessor = mAccessor;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

}
