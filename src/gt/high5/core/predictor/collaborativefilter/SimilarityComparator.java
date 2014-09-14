package gt.high5.core.predictor.collaborativefilter;

import gt.high5.database.model.Table;

import java.util.Comparator;
import java.util.List;

/**
 * @author GT
 * 
 * @param <T>
 */
public interface SimilarityComparator<T> {
	/**
	 * calculate the similarity of set a and set b, who contains same type of
	 * record
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public float getSimilarity(List<Table> a, List<Table> b);

	/**
	 * @return a comparator to sort list of T to prepare for similarity
	 *         calculation
	 */
	public Comparator<T> getSorter();
}
