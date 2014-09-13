package gt.high5.core.predictor.naivebayes;

import gt.high5.core.predictor.PredictContext;

public interface NaiveBayesData {
	public float getDefaultPossibility(PredictContext context);
}
