package gt.high5.database.table;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "")
	private String connection = "";

	@Override
	public boolean queryForRecord(RecordContext context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != manager) {
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (null == info || (!info.isConnectedOrConnecting())) {
				setConnection("NONE");
			} else {
				setConnection(info.getTypeName() + "_" + info.getSubtypeName());
			}
			setPid(context.getTotal().getId());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean queryForRead(RecordContext context) {
		return queryForRecord(context);
	}

	@Override
	public boolean initDefault(RecordContext context) {
		count = 1;
		return queryForRecord(context);
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.2f;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}
}
