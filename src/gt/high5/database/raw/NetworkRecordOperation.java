package gt.high5.database.raw;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import gt.high5.core.service.RecordContext;

public class NetworkRecordOperation implements RecordOperation {

	@Override
	public Object queryForRecord(RecordContext context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
