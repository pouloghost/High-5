package gt.high5.database.accessor;

import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Ignore;

import java.util.ArrayList;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

	private TableParser parser = null;

	public DatabaseManager(Context context, TableParser parser) {
		super(context, parser.getFile(), null, parser.getVersion());
		this.parser = parser;
	}

	/**
	 * tables defined in record table and other no-record tables
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		ArrayList<Class<? extends Table>> tablesToInit = new ArrayList<Class<? extends Table>>();
		Class<? extends RecordTable>[] tables = parser.getTables();
		for (Class<? extends RecordTable> table : tables) {
			tablesToInit.add(table);
		}
		tablesToInit.add(Ignore.class);
		for (Class<? extends Table> clazz : tablesToInit) {
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
		onCreate(db);
	}
}
