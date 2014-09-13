package gt.high5.core.predictor.collaborativefilter;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import gt.high5.R;
import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.Predictor;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

public class CollaborativeFilterPredictor extends Predictor {
	private static int XML_ID = R.xml.cf_tables;

	@Override
	public ArrayList<Table> predictPossibility(PredictContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<RecordTable> getRelativeRecords(PredictContext context,
			Total total) {
		return null;
	}

	@Override
	public DatabaseAccessor getAccessor(Context context) {
		return DatabaseAccessor.getAccessor(context, XML_ID);
	}

}
