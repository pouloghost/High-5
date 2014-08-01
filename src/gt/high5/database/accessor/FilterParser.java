package gt.high5.database.accessor;

import gt.high5.database.filter.Filter;
import gt.high5.database.model.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author GT
 * 
 *         loader to parsing filter xml
 */
public class FilterParser {
	/**
	 * @author GT Tags allowed in xml
	 */
	private static enum TAGS {
		filters, filter, param, method
	}

	/**
	 * @author GT Attributes allowed in xml
	 */
	private static enum ATTR {
		name, count, clazz, pack
	}

	private String mPackage = null;

	private ArrayList<Filter> mFilters = null;

	public FilterParser(XmlPullParser parser) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			XmlPullParserException, IOException, IllegalArgumentException,
			InvocationTargetException {
		setFilters(loadFilters(parser));
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Filter> loadFilters(XmlPullParser parser)
			throws XmlPullParserException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		ArrayList<Filter> result = null;

		int eventType = parser.getEventType();
		Method method = null;
		Filter filter = null;
		ArrayList<String> params = null;
		Class<? extends Filter> clazz = null;

		while (XmlPullParser.END_DOCUMENT != eventType) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				result = new ArrayList<Filter>();
				break;
			case XmlPullParser.START_TAG:
				String startName = parser.getName();
				TAGS startTag = TAGS.valueOf(startName);
				int size = 0;
				switch (startTag) {
				case filters:
					size = parser.getAttributeCount();
					for (int i = 0; i < size; ++i) {
						ATTR attr = ATTR.valueOf(parser.getAttributeName(i));
						switch (attr) {
						case pack:
							mPackage = parser.getAttributeValue(i);
							break;
						default:
							break;
						}
					}
					break;
				case filter:
					// init a filter
					// class
					String simpleName = parser.getAttributeValue(null,
							ATTR.clazz.toString());
					clazz = (Class<? extends Filter>) Class.forName(mPackage
							+ "." + simpleName);
					// instance
					filter = (Filter) clazz.newInstance();
					break;
				case method:
					// method
					String methodName = parser.getAttributeValue(null,
							ATTR.name.toString());
					// parameter count
					int count = Integer.parseInt(parser.getAttributeValue(null,
							ATTR.count.toString()));
					Class<?>[] parameterTypes = new Class[count];
					for (int i = 0; i < count; ++i) {
						parameterTypes[i] = String.class;
					}
					method = ClassUtils.methodForName(clazz, Filter.class,
							methodName, parameterTypes);
					break;
				case param:
					params = new ArrayList<String>();
					break;
				default:
					break;
				}
				break;
			case XmlPullParser.TEXT:
				// for texts in param tag
				params.add(parser.getText());
				break;
			case XmlPullParser.END_TAG:
				String endName = parser.getName();
				TAGS endTag = TAGS.valueOf(endName);
				switch (endTag) {
				case filters:
					break;
				case filter:
					result.add(filter);
					filter = null;
					break;
				case param:
					break;
				case method:
					if (null != method) {
						method.invoke(filter, params.toArray());
					}
					method = null;
					params = null;
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

	public ArrayList<Filter> getFilters() {
		return mFilters;
	}

	public void setFilters(ArrayList<Filter> mFilters) {
		this.mFilters = mFilters;
	}
}
