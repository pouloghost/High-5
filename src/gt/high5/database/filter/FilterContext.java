package gt.high5.database.filter;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class FilterContext {
	private ApplicationInfo info = null;
	private Context context = null;

	public ApplicationInfo getInfo() {
		return info;
	}

	public void setInfo(ApplicationInfo info) {
		this.info = info;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
