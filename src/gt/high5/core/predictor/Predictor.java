package gt.high5.core.predictor;

import gt.high5.database.model.Table;

import java.util.ArrayList;

/**
 * @author GT
 * 
 *         a strategy for calculating the possibility of tables
 */
public interface Predictor {
	/**
	 * input a context calculate all the possibilities of tables and return the
	 * tables
	 * 
	 * @param context
	 * @return totals with predicted possibility
	 */
	public ArrayList<Table> predictPossibility(PredictContext context);
}
