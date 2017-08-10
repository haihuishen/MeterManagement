package android.serialport;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 获取线程池的类(管理器)
 */
public class ThreadManager {

	private static ThreadManager threadManager;
	private static Object object = new Object();
	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(5);

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 1,
			TimeUnit.HOURS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

	public void ThThreadManager() {

	};


	public static ThreadManager getInstance() {
		if (null == threadManager) {
			synchronized (object) {
				if (null == threadManager) {
					threadManager = new ThreadManager();
				}
			}
		}
		return threadManager;
	}


	/**
	 * 将任务提交给线程池
	 *
	 * @param task 要执行的任务(Runnable类)
	 */
	public void submit(Runnable task) {
		executor.submit(task);
	}
}
