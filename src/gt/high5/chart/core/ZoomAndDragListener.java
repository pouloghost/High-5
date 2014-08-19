package gt.high5.chart.core;

import android.view.MotionEvent;
import android.view.View;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class ZoomAndDragListener implements View.OnTouchListener {

	private XYSeries mSeries = null;
	// touches
	private float mDownX = -1f;
	private float mZoom = -1f;

	// bounds
	float minX = -1f;
	float maxX = -1f;

	public ZoomAndDragListener(XYSeries series) {
		mSeries = series;
		minX = mSeries.getX(0).floatValue();
		maxX = mSeries.getX(mSeries.size() - 1).floatValue();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:// single touch
			mDownX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			mDownX = -1f;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:// multiple touches
			mZoom = distance(event);
			mZoom = mZoom > 5f ? mZoom : -1f;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mZoom = -1f;
			break;
		case MotionEvent.ACTION_MOVE:
			switch (event.getPointerCount()) {
			case 1:// single
				drag(event, (XYPlot) v);
				break;
			case 2:// mutilple
				zoom(event, (XYPlot) v);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return true;
	}

	private void zoom(MotionEvent event, XYPlot v) {
		if (-1f == mZoom) {
			return;
		}
		float left = v.getCalculatedMinX().floatValue();
		float right = v.getCalculatedMaxX().floatValue();
		float span = right - left;
		float mid = (left + right) / 2f;
		float zoom = distance(event);
		float scale = mZoom / zoom;
		float step = span * scale / 2f;

		left = Math.max(mid - step, minX);
		right = Math.min(mid + step, maxX);

		v.setDomainBoundaries(left, right, BoundaryMode.FIXED);
		v.redraw();
		mZoom = zoom;
	}

	private void drag(MotionEvent event, XYPlot v) {
		if (-1f == mDownX) {
			return;
		}
		float left = v.getCalculatedMinX().floatValue();
		float right = v.getCalculatedMaxX().floatValue();
		float span = right - left;
		float pan = event.getX() - mDownX;

		// move step
		float ratio = span / v.getWidth();
		float step = ratio * pan;
		if (step > 0) {// move right
			right = Math.min(right - step, maxX);
			left = right - span;
		} else {
			left = Math.max(left - step, minX);
			right = left + span;
		}
		mDownX = event.getX();
		// move
		v.setDomainBoundaries(left, right, BoundaryMode.FIXED);
		v.redraw();
	}

	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

}
