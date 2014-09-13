package gt.high5.core.predictor.collaborativefilter;

import gt.high5.database.model.Table;

import java.util.ArrayList;
import java.util.Comparator;

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
	public float getSimilarity(ArrayList<Table> a, ArrayList<Table> b);

	/**
	 * @return a comparator to sort list of T to prepare for similarity
	 *         calculation
	 */
	public Comparator<T> getSorter();
}
