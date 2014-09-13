package gt.high5.core.predictor.collaborativefilter;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author GT
 * 
 * @param <T>
 */
public interface SimilarityComparator<TM, TC> {
	/**
	 * calculate the similarity of set a and set b, who contains same type of
	 * record
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public float getSimilarity(ArrayList<TM> a, ArrayList<TM> b);

	/**
	 * @return a comparator to sort list of T to prepare for similarity
	 *         calculation
	 */
	public Comparator<TC> getSorter();
}
