package gt.high5.core.predictor;

import gt.high5.database.model.Table;
import gt.high5.database.table.Total;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class MultiThreadPredictor extends Predictor {
	protected List<Table> execute(List<Table> allTotals,
			List<Callable<Total>> tasks) {
		ExecutorService executor = Executors.newCachedThreadPool();
		try {
			allTotals.clear();
			List<Future<Total>> results = executor.invokeAll(tasks);
			for (Future<Total> result : results) {
				try {
					allTotals.add(result.get());
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			allTotals = null;
			e.printStackTrace();
		}
		return allTotals;
	}
}
