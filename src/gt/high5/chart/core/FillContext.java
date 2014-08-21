package gt.high5.chart.core;

import gt.high5.database.model.RecordTable;
import gt.high5.database.table.Total;
import android.content.Context;
import android.view.View;

import com.androidplot.pie.PieChart;
import com.androidplot.xy.XYPlot;

public class FillContext {
	private int mIndex = -1;
	private Context mContext = null;
	private Total mTotal = null;
	private Class<? extends RecordTable> mRecord = null;
	private XYPlot mXyPlot = null;
	private PieChart mPieChart = null;
	private View mView2Show = null;

	public FillContext(int index, XYPlot xyPlot, PieChart pieChart,
			Context context, Total total, Class<? extends RecordTable> record) {
		setXyPlot(xyPlot);
		setPieChart(pieChart);
		setIndex(index);
		setContext(context);
		setRecord(record);
		setTotal(total);
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public XYPlot getXyPlot() {
		return mXyPlot;
	}

	public void setXyPlot(XYPlot mXyPlot) {
		this.mXyPlot = mXyPlot;
	}

	public PieChart getPieChart() {
		return mPieChart;
	}

	public void setPieChart(PieChart mPieChart) {
		this.mPieChart = mPieChart;
	}

	public View getView2Show() {
		return mView2Show;
	}

	public void setView2Show(View mView2Show) {
		this.mView2Show = mView2Show;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	public Total getTotal() {
		return mTotal;
	}

	public void setTotal(Total mTotal) {
		this.mTotal = mTotal;
	}

	public Class<? extends RecordTable> getRecord() {
		return mRecord;
	}

	public void setRecord(Class<? extends RecordTable> mRecord) {
		this.mRecord = mRecord;
	}

}
