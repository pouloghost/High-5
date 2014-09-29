package gt.high5.database.reducer;

import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;

import java.util.List;
import java.util.Map;

public abstract class Reducer {
	private static Reducer[] reducers = { new EntropyReducer() };

	public static Reducer[] getReducers() {
		return reducers;
	}

	public abstract List<Class<? extends RecordTable>> shouldRead(
			Map<Class<? extends RecordTable>, List<Table>> records);
}
