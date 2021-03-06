package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.activity.CancelableTask;
import gt.high5.core.service.ReadService;
import gt.high5.database.model.RecordTable;
import gt.high5.database.parser.TableParser;
import gt.high5.database.table.Total;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
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
public class RecordDetailFragment extends Fragment implements CancelableTask {
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

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.record_detail_main, container,
				false);
		mPager = (ViewPager) root.findViewById(R.id.record_detail_view_pager);
		mPager.setAdapter(new ChartFragmentAdapter(getChildFragmentManager()));
		return root;
	}

	@Override
	public void cancel() {
		((CancelableTask) mPager.getChildAt(mPager.getCurrentItem())).cancel();
	}

	@Override
	public boolean isCancelable() {
		return ((CancelableTask) mPager.getChildAt(mPager.getCurrentItem()))
				.isCancelable();
	}

	class ChartFragmentAdapter extends FragmentPagerAdapter {

		private Class<? extends RecordTable>[] mRecords = null;

		// private DatabaseAccessor mAccessor = null;
		private TableParser mParser = null;

		@SuppressWarnings("unchecked")
		public ChartFragmentAdapter(FragmentManager fm) {
			super(fm);

			ReadService readService = ReadService.getReadService(getActivity()
					.getApplicationContext());
			mParser = readService.getTableParser();
			Class<? extends RecordTable>[] tables = readService.getTables();
			mRecords = new Class[tables.length];
			for (int i = 0; i < tables.length - 1; ++i) {
				if (Total.class == tables[i]) {// delete total
					tables[i] = tables[tables.length - 1];
				}
				Class<? extends RecordTable> clazz = tables[i];
				int j = i - 1;
				for (; j >= 0; --j) {
					if (clazz.getSimpleName().compareTo(
							tables[j].getSimpleName()) < 0) {
						tables[j + 1] = tables[j];
					} else {
						++j;
						break;
					}
				}
				j = j < 0 ? 0 : j;
				tables[j] = clazz;
			}
			// for (int i = 0; i < mRecords.length; ++i) {
			// mRecords[i] = tables[i];
			// }
			System.arraycopy(tables, 0, mRecords, 1, mRecords.length - 1);
			mRecords[0] = Total.class;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = position == 0 ? new TotalDetailPagerFragment()
					: new RecordDetailPagerFragment();

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
			return mParser.getTableTitle(mRecords[position]);
		}

	}
}
