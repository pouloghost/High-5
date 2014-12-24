package gt.high5.collector;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String mapToJson(Map map) {
		StringBuilder stringBuilder = new StringBuilder("{");
		Iterator<Object> keyIterator = map.keySet().iterator();
		while (keyIterator.hasNext()) {
			Object key = keyIterator.next();
			Object object = map.get(key);
			stringBuilder.append(key.toString());
			stringBuilder.append(":");
			if (object instanceof String) {
				stringBuilder.append("'");
				stringBuilder.append(object.toString());
				stringBuilder.append("'");
			} else {
				stringBuilder.append(object.toString());
			}
			stringBuilder.append(",");
		}
		stringBuilder.setCharAt(stringBuilder.length() - 1, '}');
		return stringBuilder.toString();
	}

	public static String stringListToJson(List<String> list) {
		StringBuilder stringBuilder = new StringBuilder("[");
		for (String val : list) {
			stringBuilder.append("'");
			stringBuilder.append(val);
			stringBuilder.append("'");
		}
		stringBuilder.setCharAt(stringBuilder.length() - 1, ']');
		return stringBuilder.toString();
	}
}
