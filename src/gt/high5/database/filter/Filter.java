package gt.high5.database.filter;

/**
 * @author GT
 * 
 *         judge whether a package should be ignored as default
 */
public interface Filter {
	public boolean shouldIgnore(FilterContext context);
}
