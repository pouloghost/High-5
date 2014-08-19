package gt.high5.chart.core;

import gt.high5.R;

import java.util.HashMap;

import com.androidplot.pie.SegmentFormatter;

/**
 * @author GT
 * 
 *         each table will have a data filler to display the data in a chart
 */
public abstract class DataFiller {

	public enum CHART_TYPE {
		BAR, LINE, PIE
	};

	protected static SegmentFormatter[] pieFormatters = null;

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

	protected int initSegmentFormatters(int size) {
		if (null == pieFormatters) {
			pieFormatters = new SegmentFormatter[] { new SegmentFormatter(),
					new SegmentFormatter(), new SegmentFormatter(),
					new SegmentFormatter(), new SegmentFormatter(), };
			// init formatters
			pieFormatters[0].configure(mContext.getContext(),
					R.xml.pie_segment_0);
			pieFormatters[1].configure(mContext.getContext(),
					R.xml.pie_segment_1);
			pieFormatters[2].configure(mContext.getContext(),
					R.xml.pie_segment_2);
			pieFormatters[3].configure(mContext.getContext(),
					R.xml.pie_segment_3);
			pieFormatters[3].configure(mContext.getContext(),
					R.xml.pie_segment_4);
		}
		int colors = 2;
		while (0 == (size - 1) % colors) {
			++colors;
		}

		return colors;
	}

	/**
	 * @author GT
	 * 
	 *         interface for filling up a chart
	 */
	public interface ViewFiller {
		public boolean fillView();
	}
}
