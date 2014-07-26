package gt.high5.database.model;

/**
 * @author GT
 * 
 *         most common representor for a table in DB
 */
public abstract class Table {

	public abstract String getCreator();

	public abstract String C();

	public abstract String R();

	public abstract String U(Table select);

	public abstract String D();

	/*
	 * for new instances
	 */
	public abstract RecordTable clone();

	/*
	 * id field and pid field
	 */
	public abstract int getId();

	public abstract void setId(int id);
}
