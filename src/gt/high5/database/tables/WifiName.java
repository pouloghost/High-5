package gt.high5.database.tables;

import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * @author GT
 * 
 *         wifi bssid record
 * 
 *         indicating which ap is connected
 */
public class WifiName extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "")
	private String bssid = "";

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	@Override
	public boolean currentQueryStatus(RecordContext context) {
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
			setBssid(bssid);

			return true;
		} else {
			return false;
		}
	}
}
