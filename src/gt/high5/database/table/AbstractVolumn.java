package gt.high5.database.table;

import android.content.Context;
import android.media.AudioManager;
import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

public abstract class AbstractVolumn extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int percent = -1;

	@Override
	public boolean initDefault(RecordContext context) {
		count = 1;
		return currentQueryStatus(context);
	}

	@Override
	public boolean currentQueryStatus(RecordContext context) {
		AudioManager manager = (AudioManager) context.getContext()
				.getSystemService(Context.AUDIO_SERVICE);
		int type = getType();
		double max = manager.getStreamMaxVolume(type);
		double current = manager.getStreamVolume(type);
		percent = (int) (current / max * 10);
		return true;
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.2f;
	}

	protected abstract int getType();

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
}
