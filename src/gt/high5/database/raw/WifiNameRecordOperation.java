package gt.high5.database.raw;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import gt.high5.core.service.RecordContext;

public class WifiNameRecordOperation implements RecordOperation {

	@Override
	public Object queryForRecord(RecordContext context) {
		WifiManager manager = (WifiManager) context.getContext()
				.getSystemService(Context.WIFI_SERVICE);
		if (null != manager) {
			WifiInfo info = manager.getConnectionInfo();
			String bssid = info.getBSSID();
			if (null == bssid) {
				if (manager.isWifiEnabled()) {
					bssid = "ON";
				} else {
					bssid = "OFF";
				}
			}
			return bssid;
		} else {
			return null;
		}
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

}
