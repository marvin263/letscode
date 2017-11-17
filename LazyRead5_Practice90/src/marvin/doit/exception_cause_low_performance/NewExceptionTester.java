package marvin.doit.exception_cause_low_performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 * xen虚拟机,5.5G内存;8核CPU
 * LOOP = 10000000
 * THREADS = 10
 * o:       45284 
 * e:       205482 
 * exte:    16731
 * </pre>
 * 
 * k
 * 
 * @author li.jinl 2010-7-9 上午09:16:14
 */
public class NewExceptionTester {

    private static final int             LOOP                 = 10000000;                        // 单次循环数量
    private static final int             THREADS              = 10;                              // 并发线程数量

    private static final List<Long>      newObjectTimes       = new ArrayList<Long>(THREADS);
    private static final List<Long>      newExceptionTimes    = new ArrayList<Long>(THREADS);
    private static final List<Long>      newExtExceptionTimes = new ArrayList<Long>(THREADS);
    private static final List<Long>      newExtSyncExceptionTimes = new ArrayList<Long>(THREADS);

    private static final ExecutorService POOL                 = Executors.newFixedThreadPool(30);

    public static void main(String[] args) throws Exception {
        List<Callable<Boolean>> all = new ArrayList<Callable<Boolean>>();
        all.addAll(tasks(new NewObject()));
        all.addAll(tasks(new NewException()));
        all.addAll(tasks(new NewExtException()));
        all.addAll(tasks(new NewExtSyncException()));

        POOL.invokeAll(all);

        System.out.println("o:\t\t" + total(newObjectTimes));
        System.out.println("e:\t\t" + total(newExceptionTimes));
        System.out.println("exte:\t\t" + total(newExtExceptionTimes));
        System.out.println("extsynce:\t\t" + total(newExtSyncExceptionTimes));

        POOL.shutdown();
    }

    private static List<Callable<Boolean>> tasks(Callable<Boolean> c) {
        List<Callable<Boolean>> list = new ArrayList<Callable<Boolean>>(THREADS);
        for (int i = 0; i < THREADS; i++) {
            list.add(c);
        }
        return list;
    }

    private static long total(List<Long> list) {
        long sum = 0;
        for (Long v : list) {
            sum += v;
        }
        return sum;
    }

    public static class NewObject implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            long start = System.currentTimeMillis();
            for (int i = 0; i < LOOP; i++) {
                new CustomObject("");
            }
            newObjectTimes.add(System.currentTimeMillis() - start);
            return true;
        }

    }

    public static class NewException implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            long start = System.currentTimeMillis();
            for (int i = 0; i < LOOP; i++) {
                new CustomException("");
            }
            newExceptionTimes.add(System.currentTimeMillis() - start);
            return true;
        }

    }

    public static class NewExtException implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            long start = System.currentTimeMillis();
            for (int i = 0; i < LOOP; i++) {
                new ExtCustomException("");
            }
            newExtExceptionTimes.add(System.currentTimeMillis() - start);
            return true;
        }

    }

    public static class NewExtSyncException implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            long start = System.currentTimeMillis();
            for (int i = 0; i < LOOP; i++) {
                new ExtSyncCustomException("");
            }
            newExtSyncExceptionTimes.add(System.currentTimeMillis() - start);
            return true;
        }

    }

    /**
     * 自定义java对象.
     * 
     * @author li.jinl 2010-7-9 上午11:28:27
     */
    public static class CustomObject extends HashMap {

        private static final long serialVersionUID = 5176739397156548105L;

        private String            message;

        public CustomObject(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    /**
     * 自定义普通的Exception对象
     * 
     * @author li.jinl 2010-7-9 上午11:28:58
     */
    public static class CustomException extends Exception {

        private static final long serialVersionUID = -6879298763723247455L;

        private String            message;

        public CustomException(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    /**
     * <pre>
     * 自定义改进的Exception对象 覆写了 fillInStackTrace方法
     * 1. 不填充stack
     * 2. 取消同步
     * </pre>
     * 
     * @author li.jinl 2010-7-9 上午11:29:12
     */
    public static class ExtCustomException extends Exception {

        private static final long serialVersionUID = -6879298763723247455L;

        private String            message;

        public ExtCustomException(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    /**
     * <pre>
     * 自定义改进的Exception对象 覆写了 fillInStackTrace方法
     * 1. 不填充stack
     * </pre>
     * 
     * @author li.jinl 2010-7-9 上午11:29:12
     */
    public static class ExtSyncCustomException extends Exception {

        private static final long serialVersionUID = -6879298743723247455L;

        private String            message;

        public ExtSyncCustomException(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
