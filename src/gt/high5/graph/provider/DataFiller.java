package gt.high5.graph.provider;

import java.util.HashMap;

/**
 * @author GT
 * 
 *         each table will have a data filler to display the data in a chart
 */
public abstract class DataFiller {

	public enum CHART_TYPE {
		BAR, LINE, PIE
	};

	protected HashMap<CHART_TYPE, ViewFiller> mFillers = new HashMap<CHART_TYPE, ViewFiller>();
	protected FillContext mContext = null;

	public DataFiller() {
		addFillers();
	}

	protected abstract void addFillers();

	/**
	 * strategy pattern
	 * 
	 * @param context
	 */
	public void fillView(FillContext context) {
		mContext = context;
		ViewFiller filler = mFillers.get(context.getType());
		if (null != filler) {
			if (!filler.fillView()) {
				context.setView(null);
			}
		}
	}

	/**
	 * @author GT
	 * 
	 *         interface for filling up a chart
	 */
	protected interface ViewFiller {
		public boolean fillView();
	}
}
