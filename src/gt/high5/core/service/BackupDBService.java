package gt.high5.core.service;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import android.content.Context;

public class BackupDBService {
	public static void backup(Context context) {
		DatabaseAccessor.getAccessor(context, R.xml.tables).backup(context);
	}

	public static void restore(Context context) {
		DatabaseAccessor.getAccessor(context, R.xml.tables).restore(context);
	}
}
