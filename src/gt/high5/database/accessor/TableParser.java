package gt.high5.database.accessor;

import gt.high5.database.model.Table;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TableParser {
	private static enum TAGS {
		tables, table
	}

	private static enum ATTR {
		file, version, pack, clazz
	}

	private int mVersion = 1;
	private String mFile = null;
	private String mPackage = null;
	private List<Class<? extends Table>> tables = null;

	public TableParser(XmlPullParser parser) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			XmlPullParserException, IOException {
		setTables(loadTables(parser));
	}

	public List<Class<? extends Table>> loadTables(XmlPullParser parser)
			throws XmlPullParserException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		LinkedList<Class<? extends Table>> result = null;

		int eventType = parser.getEventType();

		while (XmlPullParser.END_DOCUMENT != eventType) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				result = new LinkedList<Class<? extends Table>>();
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
							Class<Table> clazz = (Class<Table>) Class
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

	public List<Class<? extends Table>> getTables() {
		return tables;
	}

	public void setTables(List<Class<? extends Table>> tables) {
		this.tables = tables;
	}
}
