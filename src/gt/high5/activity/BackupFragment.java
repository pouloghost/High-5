package gt.high5.activity;

import gt.high5.R;
import gt.high5.core.service.BackupDBService;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BackupFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.backup_layout, null);
		((Button) root.findViewById(R.id.backup_backup_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						BackupDBService.backup(getActivity()
								.getApplicationContext());
					}
				});
		((Button) root.findViewById(R.id.backup_restore_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						BackupDBService.restore(getActivity()
								.getApplicationContext());
					}
				});
		return root;
	}

}
