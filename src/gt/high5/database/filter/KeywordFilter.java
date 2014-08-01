package gt.high5.database.filter;

import java.util.ArrayList;

public abstract class KeywordFilter implements Filter {
	protected ArrayList<String> keywords = new ArrayList<String>();

	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	public void removeKeyword(String keyword) {
		keywords.remove(keyword);
	}
}
