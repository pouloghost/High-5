package gt.high5.activity;

import gt.high5.R;
import gt.high5.core.service.DBOperationService;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DatabaseOperationFragment extends Fragment {

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.db_operation_layout, null);
		((Button) root.findViewById(R.id.db_backup_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						DBOperationService.backup(getActivity());
					}
				});
		((Button) root.findViewById(R.id.db_restore_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						DBOperationService.restore(getActivity());
					}
				});
		((Button) root.findViewById(R.id.db_clean_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						DBOperationService.clean(getActivity());
					}
				});
		return root;
	}

}
