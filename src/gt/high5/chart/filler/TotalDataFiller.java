package gt.high5.chart.filler;

import gt.high5.chart.core.DataFiller;

/**
 * @author GT
 * 
 *         a data filler for total
 */
public class TotalDataFiller extends DataFiller {

	@Override
	protected void addFillers() {
		mFillers.put(CHART_TYPE.PIE, new ViewFiller() {

			@Override
			public boolean fillView() {
				return false;
			}
		});
		mFillers.put(CHART_TYPE.BAR, new ViewFiller() {

			@Override
			public boolean fillView() {
				return false;
			}
		});
		mFillers.put(CHART_TYPE.LINE, new ViewFiller() {

			@Override
			public boolean fillView() {
				return false;
			}
		});
	}
}
