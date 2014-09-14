package gt.high5.database.accessor;

import gt.high5.core.predictor.collaborativefilter.SimilarityComparator;
import gt.high5.core.predictor.naivebayes.NaiveBayesData;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.TableInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
		file, version, model_pack, filler_pack, naive_data_pack, sim_pack, clazz, title, filler, weight, naive_data, comparator
	}

	private int mVersion = 1;
	private String mFile = null;
	// model for representing data in database
	private String mModelPackage = null;
	// parser for filling up data for graphs
	private String mParserPackage = null;
	// naive data package
	private String mNaiveDataPackage = null;
	// collaborative filter similarity comparator package
	private String mSimilarityPackage = null;
	/**
	 * table types in xml
	 */
	private HashMap<Class<? extends RecordTable>, TableInfo> mTables = null;

	public TableParser(XmlPullParser parser) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			XmlPullParserException, IOException {
		setTables((HashMap<Class<? extends RecordTable>, TableInfo>) loadTables(parser));
	}

	@SuppressWarnings("unchecked")
	public Map<Class<? extends RecordTable>, TableInfo> loadTables(
			XmlPullParser parser) throws XmlPullParserException, IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		HashMap<Class<? extends RecordTable>, TableInfo> result = null;

		int eventType = parser.getEventType();

		TableInfo info = null;
		Class<RecordTable> clazz = null;

		while (XmlPullParser.END_DOCUMENT != eventType) {

			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				result = new HashMap<Class<? extends RecordTable>, TableInfo>();
				break;
			case XmlPullParser.START_TAG:
				String startName = parser.getName();
				TAGS startTag = TAGS.valueOf(startName);
				int size = 0;
				switch (startTag) {
				case tables:
					size = parser.getAttributeCount();
					for (int i = 0; i < size; ++i) {
						ATTR attr = ATTR.valueOf(parser.getAttributeName(i));
						switch (attr) {
						case file:
							mFile = parser.getAttributeValue(i);
							break;
						case model_pack:
							mModelPackage = parser.getAttributeValue(i);
							break;
						case filler_pack:
							mParserPackage = parser.getAttributeValue(i);
							break;
						case naive_data_pack:
							mNaiveDataPackage = parser.getAttributeValue(i);
							break;
						case sim_pack:
							mSimilarityPackage = parser.getAttributeValue(i);
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
					info = new TableInfo();
					for (int i = 0; i < size; ++i) {
						ATTR attr = ATTR.valueOf(parser.getAttributeName(i));
						switch (attr) {
						case clazz:
							clazz = (Class<RecordTable>) Class
									.forName(mModelPackage + "."
											+ parser.getAttributeValue(i));
							break;
						case title:
							info.setTitle(parser.getAttributeValue(i));
							break;
						case filler:
							info.setFiller(Class.forName(mParserPackage + "."
									+ parser.getAttributeValue(i)));
							break;
						case weight:
							info.setWeight(Integer.valueOf(parser
									.getAttributeValue(i)));
							break;
						case naive_data:
							info.setNaiveBayesData((NaiveBayesData) Class
									.forName(
											mNaiveDataPackage
													+ "."
													+ parser.getAttributeValue(i))
									.newInstance());
							break;
						case comparator:
							info.setSimilarityComparator((SimilarityComparator<?>) Class
									.forName(
											mSimilarityPackage
													+ "."
													+ parser.getAttributeValue(i))
									.newInstance());
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
				String endName = parser.getName();
				TAGS endTag = TAGS.valueOf(endName);
				switch (endTag) {
				case tables:
					break;
				case table:
					result.put(clazz, info);
					clazz = null;
					info = null;
					break;
				default:
					break;
				}
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

	@SuppressWarnings("unchecked")
	public Class<? extends RecordTable>[] getTables() {
		Class<? extends RecordTable>[] typeArray;
		typeArray = new Class[0];
		return (Class<? extends RecordTable>[]) mTables.keySet().toArray(
				typeArray);
	}

	/**
	 * get info about a record type
	 * 
	 * @param clazz
	 *            record class
	 * @return record info
	 */
	public TableInfo getInfo(Class<? extends RecordTable> clazz) {
		return mTables.get(clazz);
	}

	public String getTableTitle(Class<? extends RecordTable> clazz) {
		return getInfo(clazz).getTitle();
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
		return mModelPackage;
	}

	public void setPackage(String mPackage) {
		this.mModelPackage = mPackage;
	}

	private void setTables(
			HashMap<Class<? extends RecordTable>, TableInfo> tables) {
		mTables = tables;
	}

}
