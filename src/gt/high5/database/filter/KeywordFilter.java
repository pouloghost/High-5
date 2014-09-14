package gt.high5.database.filter;

import java.util.LinkedList;

public abstract class KeywordFilter implements Filter {
	protected LinkedList<String> keywords = new LinkedList<String>();

	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	public void removeKeyword(String keyword) {
		keywords.remove(keyword);
	}
}
