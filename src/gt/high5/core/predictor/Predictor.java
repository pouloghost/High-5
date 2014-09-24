package gt.high5.core.predictor;

import gt.high5.chart.core.DataFiller;
import gt.high5.core.predictor.collaborativefilter.CollaborativeFilterPredictor;
import gt.high5.core.service.PreferenceService;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableInfo;
import gt.high5.database.parser.TableParser;
import gt.high5.database.parser.TableParserProxy;
import gt.high5.database.table.Total;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

/**
 * @author GT
 * 
 *         a strategy for calculating the possibility of tables
 */
public abstract class Predictor implements TableParserProxy {

	public interface Callbacks {
		public void onPredictorChanged();
	}

	private static Predictor instance = new CollaborativeFilterPredictor();

	public static Predictor getPredictor() {
		return instance;
	}

	public static void setPredictor(String clazz, Context context)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		instance = (Predictor) Class.forName(clazz).newInstance();
		instance.initTableParser(context);
	}

	private static LinkedList<Callbacks> callbacks = new LinkedList<Callbacks>();

	public static void registerCallback(Callbacks callback) {
		callbacks.add(callback);
	}

	public static void unregisterCallback(Callbacks callback) {
		callbacks.remove(callback);
	}

	private TableParser mTableParser = null;

	/**
	 * input a context calculate all the possibilities of tables and return the
	 * tables
	 * 
	 * @param context
	 * @return totals with predicted possibility
	 */
	public abstract List<Table> predictPossibility(PredictContext context);

	/**
	 * get all records needed to calculate the possibility of total under
	 * context
	 * 
	 * @param context
	 * @param total
	 * @return
	 */
	public abstract Collection<RecordTable> getRelativeRecords(
			PredictContext context, Total total);

	/**
	 * @return the threshold indicating the nearly impossible status
	 */
	public abstract float getMinThreshold();

	public abstract DatabaseAccessor getAccessor(Context context);

	protected abstract int getXmlId();

	// --------table proxy
	@Override
	public TableParser getTableParser() {

		return mTableParser;
	}

	@Override
	public void setTableParser(TableParser mTableParser) {
		this.mTableParser = mTableParser;
	}

	public TableParser initTableParser(Context context) {
		if (null == mTableParser) {
			try {
				mTableParser = new TableParser(context.getResources().getXml(
						getXmlId()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return mTableParser;
	}

	@Override
	public Class<? extends RecordTable>[] getTables() {
		return getTableParser().getTables();
	}

	@Override
	public DataFiller getDataFiller(Class<? extends RecordTable> clazz)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException {
		return (DataFiller) getTableParser().getInfo(clazz).getFiller()
				.getDeclaredConstructor().newInstance();
	}

	@Override
	public String getTableTitle(Class<? extends RecordTable> clazz) {
		return getTableInfo(clazz).getTitle();
	}

	@Override
	public int getTableWeight(Class<? extends RecordTable> clazz) {
		return getTableInfo(clazz).getWeight();
	}

	@Override
	public TableInfo getTableInfo(Class<? extends RecordTable> clazz) {
		return getTableParser().getInfo(clazz);
	}

	@Override
	public boolean shouldReadTable(Class<?> clazz, Context context) {
		return PreferenceService.getPreferenceReadService(context).shouldRead(
				clazz);
	}
}
