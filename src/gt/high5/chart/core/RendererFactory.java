package gt.high5.chart.core;

import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine.Type;

import android.content.Context;
import android.graphics.Color;

public class RendererFactory {

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static DefaultRenderer buildCategoryRenderer(Context context,
			int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setDisplayValues(true);
		renderer.setShowLabels(true);
		// text sizes
		renderer.setLabelsTextSize(dip2px(context, 10));
		renderer.setLegendTextSize(dip2px(context, 10));
		// colors
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		renderer.setBackgroundColor(Color.LTGRAY);
		// zoom
		renderer.setZoomButtonsVisible(false);
		renderer.setZoomEnabled(true);

		return renderer;
	}

	public static XYMultipleSeriesRenderer buildBarRenderer(Context context,
			int color) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		// text size
		renderer.setLabelsTextSize(dip2px(context, 10));
		renderer.setLegendTextSize(dip2px(context, 10));
		// color
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(color);
		renderer.addSeriesRenderer(r);
		renderer.setBackgroundColor(Color.LTGRAY);
		// style
		renderer.setMargins(new int[] { dip2px(context, 30),
				dip2px(context, 10), dip2px(context, 20), dip2px(context, 10), });
		return renderer;
	}

	public static XYMultipleSeriesRenderer buildLineRenderer(Context context,
			int color) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		// text size
		renderer.setLabelsTextSize(dip2px(context, 10));
		renderer.setLegendTextSize(dip2px(context, 10));
		renderer.setPointSize(5f);
		// style
		renderer.setMargins(new int[] { dip2px(context, 30),
				dip2px(context, 10), dip2px(context, 20), dip2px(context, 10), });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(color);
		r.setPointStyle(PointStyle.SQUARE);
		FillOutsideLine fill = new FillOutsideLine(Type.BELOW);
		fill.setColor(Color.WHITE);
		r.addFillOutsideLine(fill);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setBackgroundColor(Color.LTGRAY);
		return renderer;
	}
}
