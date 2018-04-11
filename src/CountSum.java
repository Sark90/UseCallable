import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class CountSum {
    private static final int TESTS_NUM = 3;
    private static final int MIN_THREADS_NUM = 1;
    private static final int MAX_THREADS_NUM = 30;
    private static final int MAX_ARR_SIZE = 10000000;
    private static int[] arr;
    private static HashMap<Integer, Long> durations = new HashMap<>(); //<threads number, duration>

    public static void start() {
        initArray();
        count();
        getOptimalThreadsNum();
        //nonThreadCount();
    }

    public static void nonThreadCount() {
        int s = 0;
        for (int i: arr) {
            s += i;
        }
        System.out.println("**********\nnonThreadCount: sum = " + s);
    }

    private static void getOptimalThreadsNum() {
        int optimalNum = 1;
        long minTime = durations.get(optimalNum++);
        for(int i = optimalNum; i <= MAX_THREADS_NUM; i++) {
            if (minTime > durations.get(i)) {
                minTime = durations.get(i);
                optimalNum = i;
            }
        }
        System.out.println("\nOptimal number of threads : " + optimalNum + " (time: " + minTime + " ns)");
    }

    private static void initArray() {
        System.out.print("Type the size of array: ");
        int size;
        try {
            size = new Scanner(System.in).nextInt();
        } catch (InputMismatchException e) {
            size = MAX_ARR_SIZE;
        }
        if (size<=1 || size>MAX_ARR_SIZE) size = MAX_ARR_SIZE;
        arr = new int[size];
        Random r = new Random();
        for (int i=0; i<arr.length; i++) {
            arr[i] = r.nextInt(1000);
            //System.out.print(arr[i] + "\t");
        }
        System.out.println("\nArray generated. Size: " + size);
    }

    private static void count() {
        for (int threadsNum = MIN_THREADS_NUM; threadsNum <= MAX_THREADS_NUM; threadsNum++) {
            System.out.println("\t\t--- " + threadsNum + " thread(s) ---");
            long d = 0;
            for (int testNum=1; testNum<=TESTS_NUM; testNum++) {
                ArrayList<Future<Integer>> futures = new ArrayList<>();
                System.out.println("\t--- Test #" + testNum + " ---");
                Timer timer = new Timer();
                timer.start();
                ExecutorService es = Executors.newFixedThreadPool(threadsNum);
                int part = arr.length/threadsNum + arr.length%threadsNum; //for 1st thread
                int start = 0; //for 1st thread
                for (int i=1; i<=threadsNum; i++) {
                    futures.add(es.submit(new CountThread(arr, start, part)));
                    start += part; //for other threads
                    part = arr.length/threadsNum; //for other threads
                }
                int sum = 0;
                try {
                    for (Future<Integer> f: futures) {
                        sum += f.get();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                es.shutdown();
                timer.stop();
                long duration = timer.getTime();
                d += duration;
                System.out.println("Sum = " + sum);
                System.out.println("Duration: " + duration);
            }
            long avg = d/TESTS_NUM;
            System.out.println("Average duration: " + avg);
            durations.put(threadsNum, avg);
        }
    }
}
