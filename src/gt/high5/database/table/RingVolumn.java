package gt.high5.database.table;

import android.media.AudioManager;

public class RingVolumn extends AbstractVolumn {

	@Override
	protected int getType() {
		return AudioManager.STREAM_RING;
	}

}
