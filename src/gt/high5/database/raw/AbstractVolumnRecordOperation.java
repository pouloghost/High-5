package gt.high5.database.raw;

import android.content.Context;
import android.media.AudioManager;
import gt.high5.core.service.RecordContext;

public abstract class AbstractVolumnRecordOperation implements RecordOperation {

	protected abstract int getVolumnType();

	@Override
	public Object queryForRecord(RecordContext context) {
		AudioManager manager = (AudioManager) context.getContext()
				.getSystemService(Context.AUDIO_SERVICE);
		int type = getVolumnType();
		double max = manager.getStreamMaxVolume(type);
		double current = manager.getStreamVolume(type);
		return (int) (current / max * 10);
	}

	@Override
	public Class<?> getType() {
		return Integer.class;
	}
}
