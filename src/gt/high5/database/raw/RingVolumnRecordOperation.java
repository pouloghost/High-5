package gt.high5.database.raw;

import android.media.AudioManager;

public class RingVolumnRecordOperation extends AbstractVolumn {

	@Override
	protected int getVolumnType() {
		return AudioManager.STREAM_RING;
	}

}
