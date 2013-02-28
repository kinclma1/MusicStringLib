package cz.cvut.fel.kinclma1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 21.1.13
 * Time: 3:56
 * To change this template use File | Settings | File Templates.
 */
public class Parallellization {

    public static ExecutorService executorService() {
        int cpus = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(cpus > 0 ? cpus < 5 ? cpus : 4 : 1);
    }

    public static <T> List<T> runExecutor(ExecutorService exec, Collection<Callable<T>> callables) {
        List<Future<T>> futures;
        List<T> results = new ArrayList<T>(callables.size());
        try {
            futures = exec.invokeAll(callables);
            for (Future<T> future : futures) {
                results.add(future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            if (e.getMessage().contains("Input file contains triplets.")) {
                throw new UnsupportedOperationException("Input file contains triplets.");
            } else {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static <T> List<T> executeSingleBatch(Collection<Callable<T>> callables) {
        ExecutorService exec = executorService();
        List<T> result = runExecutor(exec, callables);
        exec.shutdown();
        return result;
    }
}
