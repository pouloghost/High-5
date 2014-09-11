package gt.high5.core.predictor;

import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.table.nb.Total;

import java.util.ArrayList;

import android.content.Context;

/**
 * @author GT
 * 
 *         a strategy for calculating the possibility of tables
 */
public abstract class Predictor {

	private static Predictor instance = new NaiveBayesPredictor();

	public static Predictor getPredictor() {
		return instance;
	}

	/**
	 * input a context calculate all the possibilities of tables and return the
	 * tables
	 * 
	 * @param context
	 * @return totals with predicted possibility
	 */
	public abstract ArrayList<Table> predictPossibility(PredictContext context);

	/**
	 * get all records needed to calculate the possibility of total under
	 * context
	 * 
	 * @param context
	 * @param total
	 * @return
	 */
	public abstract ArrayList<RecordTable> getRelativeRecords(
			PredictContext context, Total total);

	public abstract DatabaseAccessor getAccessor(Context context);
}
