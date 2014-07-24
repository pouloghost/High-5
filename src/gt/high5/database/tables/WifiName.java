package gt.high5.database.tables;

import gt.high5.database.model.SimpleTable;
import gt.high5.database.model.TableAnnotation;
import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiName extends SimpleTable {

	@TableAnnotation(defaultValue = "")
	private String bssid = "";

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	@Override
	public void currentQueryStatus(Context context) {
		// TODO Auto-generated method stub
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		setBssid(manager.getConnectionInfo().getBSSID());
	}

}