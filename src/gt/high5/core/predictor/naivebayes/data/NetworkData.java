package gt.high5.core.predictor.naivebayes.data;

import gt.high5.core.predictor.PredictContext;
import gt.high5.core.predictor.naivebayes.NaiveBayesData;

public class NetworkData implements NaiveBayesData {

	@Override
	public float getDefaultPossibility(PredictContext context) {
		return 0.5f / context.getTotal().getCount();
	}

}
