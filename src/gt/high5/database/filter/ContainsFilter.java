package gt.high5.database.filter;

public class ContainsFilter extends KeywordFilter {

	@Override
	public boolean shouldIgnore(String name) {
		boolean result = false;
		for (String keyword : keywords) {
			if (result = name.contains(keyword)) {
				break;
			}
		}
		return result;
	}

}
