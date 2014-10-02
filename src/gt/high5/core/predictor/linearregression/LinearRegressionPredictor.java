package gt.high5.core.predictor.linearregression;

import gt.high5.R;
import gt.high5.core.predictor.MultiThreadPredictor;
import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.PredictorUtils;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.List;

import android.content.Context;

public class LinearRegressionPredictor extends MultiThreadPredictor {
	private static final int XML_ID = R.xml.lr_tables;

	@Override
	public List<Table> predictPossibility(PredictContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RecordTable> getRelativeRecords(PredictContext context,
			Total total) {
		return PredictorUtils.getRelativeRecordsBasedOnContext(context, total,
				getAccessor(context.getContext()), getTables());
	}

	@Override
	public float getMinThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DatabaseAccessor getAccessor(Context context) {
		return DatabaseAccessor.getAccessor(context, XML_ID);
	}

	@Override
	public void onRecordSuccess(List<RecordTable> records) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getXmlId() {
		return XML_ID;
	}

}
