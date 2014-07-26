package gt.high5.database.tables;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

public class Network extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "")
	private String connection = "";

	@Override
	public void currentQueryStatus(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (null == info || (!info.isConnectedOrConnecting())) {
			connection = "NONE";
		} else {
			connection = info.getTypeName() + "_" + info.getSubtypeName();
		}
	}
}
