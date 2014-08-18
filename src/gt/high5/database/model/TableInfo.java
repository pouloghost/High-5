package gt.high5.database.model;

/**
 * @author GT
 * 
 *         each represent a table defined in xml
 */
public class TableInfo {
	private String mTitle = null;
	private Class<?> mParser = null;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public Class<?> getParser() {
		return mParser;
	}

	public void setParser(Class<?> mParser) {
		this.mParser = mParser;
	}

}
