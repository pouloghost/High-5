package gt.high5.database.filter;

public class ContainsFilter extends KeywordFilter {

	@Override
	public boolean shouldIgnore(FilterContext context) {
		boolean result = false;
		for (String keyword : keywords) {
			if (result = context.getInfo().packageName.contains(keyword)) {
				break;
			}
		}
		return result;
	}

}
