package gt.high5.core.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import android.content.Context;

/**
 * @author ayi.zty
 * 
 *         interface for provide packages used during a period of time
 * 
 *         also the factory class for getting one
 * 
 *         this won't guarantee singleton
 */
@SuppressWarnings("unchecked")
public abstract class PackageProvider {

	protected static int MEMORY_SIZE = 10;

	private static Class<? extends PackageProvider>[] priority = null;
	static {
		priority = new Class[1];
		// priority[0] = HackPackageProvider.class;
		priority[0] = SetPackageProvider.class;
	}

	public PackageProvider() throws CannotCreateException {
	}

	/**
	 * provide a proper packageProvider based on the context condition
	 * 
	 * @param context
	 *            application context
	 * @return an instance of PackageProvider
	 */
	public static PackageProvider getPackageProvider(Context context) {
		PackageProvider provider = null;
		for (Class<? extends PackageProvider> clazz : priority) {
			try {
				provider = (PackageProvider) clazz.getDeclaredConstructor(
						(Class[]) null).newInstance();
				break;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (Exception e) {
				if (e instanceof CannotCreateException) {
					e.printStackTrace();
				}
			}
		}
		return provider;
	}

	/**
	 * get the packages used since last call on this method
	 * 
	 * @param context
	 *            application context
	 * @return packages
	 */
	public abstract Collection<LaunchInfo> getChangedPackages(Context context);

	public abstract List<String> getLastPackageOrder(Context context);

	public class CannotCreateException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
}
