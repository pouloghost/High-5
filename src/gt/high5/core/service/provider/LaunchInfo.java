package gt.high5.core.service.provider;

/**
 * @author ayi.zty
 * 
 *         holder for launch logs
 */
public class LaunchInfo {

	private String mPackage;
	private int mLaunchCount;

	public LaunchInfo(String name, int count) {
		this.mPackage = name;
		this.mLaunchCount = count;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LaunchInfo)) {
			return false;
		}
		return mPackage.equals(((LaunchInfo) o).mPackage);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return mPackage.hashCode();
	}

	public String getPackage() {
		return mPackage;
	}

	public void setPackage(String mPackage) {
		this.mPackage = mPackage;
	}

	public int getLaunchCount() {
		return mLaunchCount;
	}

	public void setLaunchCount(int mLaunchCount) {
		this.mLaunchCount = mLaunchCount;
	}

}
