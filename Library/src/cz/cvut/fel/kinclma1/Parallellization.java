package cz.cvut.fel.kinclma1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
}
