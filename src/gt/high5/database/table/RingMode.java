package gt.high5.database.table;

import android.content.Context;
import android.media.AudioManager;
import gt.high5.core.service.RecordContext;
import gt.high5.database.model.SimpleRecordTable;
import gt.high5.database.model.TableAnnotation;

public class RingMode extends SimpleRecordTable {

	@TableAnnotation(defaultValue = "-1")
	private int mode = -1;

	@Override
	public boolean initDefault(RecordContext context) {
		count = 1;
		return currentQueryStatus(context);
	}

	@Override
	public boolean currentQueryStatus(RecordContext context) {
		AudioManager manager = (AudioManager) context.getContext()
				.getSystemService(Context.AUDIO_SERVICE);
		setMode(manager.getMode());
		return true;
	}

	@Override
	public float getDefaultPossibility(Context context) {
		return 0.3f;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

}
