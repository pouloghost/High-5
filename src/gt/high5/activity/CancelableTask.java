package gt.high5.activity;

public interface CancelableTask {
	public void cancel();

	public boolean isCancelable();
}
