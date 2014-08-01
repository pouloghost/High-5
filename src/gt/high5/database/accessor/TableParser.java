package gt.high5.database.accessor;

import gt.high5.database.model.RecordTable;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author GT
 * 
 *         loader to parsing table xml
 */
public class TableParser {
	/**
	 * @author GT Tags allowed in xml
	 */
	private static enum TAGS {
		tables, table
	}

	/**
	 * @author GT Attributes allowed in xml
	 */
	private static enum ATTR {
		file, version, pack, clazz
	}

	private int mVersion = 1;
	private String mFile = null;
	private String mPackage = null;
	/**
	 * table types in xml
	 */
	private ArrayList<Class<? extends RecordTable>> mTables = null;

	public TableParser(XmlPullParser parser) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			XmlPullParserException, IOException {
		setTables(loadTables(parser));
	}

	public ArrayList<Class<? extends RecordTable>> loadTables(
			XmlPullParser parser) throws XmlPullParserException, IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		ArrayList<Class<? extends RecordTable>> result = null;

		int eventType = parser.getEventType();

		while (XmlPullParser.END_DOCUMENT != eventType) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				result = new ArrayList<Class<? extends RecordTable>>();
				break;
			case XmlPullParser.START_TAG:
				String name = parser.getName();
				TAGS tag = TAGS.valueOf(name);
				int size = 0;
				switch (tag) {
				case tables:
					size = parser.getAttributeCount();
					for (int i = 0; i < size; ++i) {
						ATTR attr = ATTR.valueOf(parser.getAttributeName(i));
						switch (attr) {
						case file:
							mFile = parser.getAttributeValue(i);
							break;
						case pack:
							mPackage = parser.getAttributeValue(i);
							break;
						case version:
							mVersion = Integer.parseInt(parser
									.getAttributeValue(i));
							break;
						default:
							break;
						}
					}
					break;
				case table:
					size = parser.getAttributeCount();
					for (int i = 0; i < size; ++i) {
						ATTR attr = ATTR.valueOf(parser.getAttributeName(i));
						switch (attr) {
						case clazz:
							@SuppressWarnings("unchecked")
							Class<RecordTable> clazz = (Class<RecordTable>) Class
									.forName(mPackage + "."
											+ parser.getAttributeValue(i));
							result.add(clazz);
							break;
						default:
							break;
						}
					}
					break;
				default:
					break;
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			case XmlPullParser.END_DOCUMENT:
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		if (null != result && 0 == result.size()) {
			result = null;
		}
		return result;
	}

	public int getVersion() {
		return mVersion;
	}

	public void setVersion(int version) {
		this.mVersion = version;
	}

	public String getFile() {
		return mFile;
	}

	public void setFile(String mFile) {
		this.mFile = mFile;
	}

	public String getPackage() {
		return mPackage;
	}

	public void setPackage(String mPackage) {
		this.mPackage = mPackage;
	}

	public ArrayList<Class<? extends RecordTable>> getTables() {
		return mTables;
	}

	public void setTables(ArrayList<Class<? extends RecordTable>> tables) {
		this.mTables = tables;
	}
}
