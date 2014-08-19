package gt.high5.activity.fragment;

public interface CancelableTask {
	public void cancel();

	public boolean isCancelable();
}
