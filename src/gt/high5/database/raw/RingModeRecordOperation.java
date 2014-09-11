package gt.high5.database.raw;

import android.content.Context;
import android.media.AudioManager;
import gt.high5.core.service.RecordContext;

public class RingModeRecordOperation implements RecordOperation {

	@Override
	public Object queryForRecord(RecordContext context) {
		return ((AudioManager) context.getContext().getSystemService(
				Context.AUDIO_SERVICE)).getMode();
	}

	@Override
	public Class<?> getType() {
		return Integer.class;
	}

}
