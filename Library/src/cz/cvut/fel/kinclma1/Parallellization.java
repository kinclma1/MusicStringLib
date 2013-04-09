package cz.cvut.fel.kinclma1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Helper class used to run ExecutorServices
 */
public class Parallellization {

    /**
     * Creates a fixed thread pool of size 1 - 4 depending on the number of CPUs
     * @return fixed thread pool of size 1 - 4 depending on the number of CPUs
     */
    public static ExecutorService executorService() {
        int cpus = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(cpus > 0 ? cpus < 5 ? cpus : 4 : 1);
    }

    /**
     * Runs a given executor service on a given collection of callables and returns the result
     * @param exec The executor service used to process the callables
     * @param callables The callables containing tasks to be executed
     * @param <T> The type returned by the callable
     * @return List of results
     */
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

    /**
     * Processes callables and returns result
     * @param callables The callables containing tasks to be executed
     * @param <T> The type returned by the callable
     * @return List of results
     */
    public static <T> List<T> executeSingleBatch(Collection<Callable<T>> callables) {
        ExecutorService exec = executorService();
        List<T> result = runExecutor(exec, callables);
        exec.shutdown();
        return result;
    }
}
