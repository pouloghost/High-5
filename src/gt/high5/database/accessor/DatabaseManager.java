package gt.high5.database.accessor;

import gt.high5.activity.MainActivity;
import gt.high5.database.model.Table;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {

	private TableParser parser = null;

	public DatabaseManager(Context context, TableParser parser) {
		super(context, parser.getFile(), null, parser.getVersion());
		this.parser = parser;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (Class<? extends Table> clazz : parser.getTables()) {
			try {
				db.execSQL(clazz.newInstance().getCreator());
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (MainActivity.isDebugging()) {
			Log.d(MainActivity.LOG_TAG, "Upgrading database from version " + oldVersion
					+ " to " + newVersion);
		}
		onCreate(db);
	}
}
