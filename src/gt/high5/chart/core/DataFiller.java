package gt.high5.chart.core;

import gt.high5.R;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.View;

/**
 * @author GT
 * 
 *         each table will have a data filler to display the data in a chart
 */
public abstract class DataFiller {
	protected static int XML_ID = R.xml.tables;

	/**
	 * @author GT
	 * 
	 *         interface for filling up a chart
	 */
	public interface ViewFiller {
		public boolean fillView();

		public View onFinish();
	}

	public enum CHART_TYPE {
		BAR, LINE, PIE
	}

	private static final int[] ALL_COLORS = { Color.BLUE, Color.CYAN,
			Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA };
	protected ArrayList<ViewFiller> mFillers = new ArrayList<ViewFiller>();
	protected FillContext mContext = null;

	public DataFiller() {
		addFillers();
	}

	/**
	 * strategy pattern
	 * 
	 * @param context
	 */
	public ViewFiller fillView(FillContext context) {

		mContext = context;
		ViewFiller filler = mFillers.get(context.getIndex());
		if (null != filler) {
			if (!filler.fillView()) {
				filler = null;
			}
		}
		return filler;
	}

	protected abstract void addFillers();

	public abstract int[] getEntryIds();

	protected int[] getColors(int size) {
		int[] result = new int[size];
		int colors = 2;
		while (0 == (size - 1) % colors && colors < ALL_COLORS.length) {
			++colors;
		}
		for (int i = 0; i < size; ++i) {
			result[i] = ALL_COLORS[i % colors];
		}
		return result;
	}
}
