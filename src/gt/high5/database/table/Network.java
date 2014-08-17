package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network extends SimpleRecordTable {

	private static String title = "";

	@TableAnnotation(defaultValue = "")
	private String connection = "";

	@Override
	public boolean currentQueryStatus(RecordContext context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != manager) {
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (null == info || (!info.isConnectedOrConnecting())) {
				connection = "NONE";
			} else {
				connection = info.getTypeName() + "_" + info.getSubtypeName();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean initDefault(RecordContext context) {
		count = 1;
		return currentQueryStatus(context);
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.2f;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		Network.title = title;
	}
}
