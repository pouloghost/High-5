package gt.high5.database.parser;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import gt.high5.chart.core.DataFiller;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.TableInfo;

public interface TableParserProxy {

	public TableParser getTableParser();

	public void setTableParser(TableParser mTableParser);

	public TableParser initTableParser(Context context);

	/**
	 * @return record tables defined in xml
	 */
	public Class<? extends RecordTable>[] getTables();

	/**
	 * proxy accessor for data filler, a factory
	 * 
	 * @param clazz
	 *            type
	 * @return data filler
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public DataFiller getDataFiller(Class<? extends RecordTable> clazz)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException;

	/**
	 * proxy accessor for title in pager, a factory
	 * 
	 * @param clazz
	 *            type
	 * @return title
	 */
	public String getTableTitle(Class<? extends RecordTable> clazz);

	public int getTableWeight(Class<? extends RecordTable> clazz);

	public TableInfo getTableInfo(Class<? extends RecordTable> clazz);

	public boolean shouldReadTable(Class<?> clazz, Context context);
}
