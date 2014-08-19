package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.database.accessor.DatabaseAccessor;
import gt.high5.database.model.RecordTable;
import gt.high5.database.table.Total;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author GT
 * 
 *         main fragment for displaying record detail
 * 
 *         with a viewpager to sliding between different type of record
 */
public class RecordDetailFragment extends Fragment {
	public static enum BUNDLE_KEYS {
		TOTAL, LABEL, CLASS
	}

	private Total mTotal = null;
	private ViewPager mPager = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// set title
		String title = getArguments().getString(BUNDLE_KEYS.LABEL.toString());
		mTotal = getArguments().getParcelable(BUNDLE_KEYS.TOTAL.toString());
		getActivity().getActionBar().setTitle(title);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.record_detail_main, container,
				false);
		mPager = (ViewPager) root.findViewById(R.id.record_detail_view_pager);
		mPager.setAdapter(new ChartFragmentAdapter(getChildFragmentManager()));
		return root;
	}

	class ChartFragmentAdapter extends FragmentPagerAdapter {

		private Class<? extends RecordTable>[] mRecords = null;
		private DatabaseAccessor mAccessor = null;

		public ChartFragmentAdapter(FragmentManager fm) {
			super(fm);

			mAccessor = DatabaseAccessor.getAccessor(getActivity()
					.getApplicationContext(), R.xml.tables);
			mRecords = mAccessor.getTables();
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			RecordDetailPagerFragment fragment = new RecordDetailPagerFragment();
			Bundle args = new Bundle();
			args.putParcelable(BUNDLE_KEYS.TOTAL.toString(), mTotal);
			args.putString(BUNDLE_KEYS.CLASS.toString(),
					mRecords[position].getName());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return mRecords.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mAccessor.getTableTitle(mRecords[position]);
		}

	}
}
