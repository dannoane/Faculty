import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static final int SIZE = 5;
    private static final int BOUND = 10;

    private static double start;
    private static double end;

    public static void main(String[] args) {

        int[] src = new int[SIZE];
        int[] dest;

        randomInit(src);
        dest = src.clone();

        List<Runnable> jobs = new ArrayList<>();
        ReentrantLock[] locks = new ReentrantLock[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            locks[i] = new ReentrantLock();
        }

        for (int index = 1; index < src.length; ++index) {
            final int i = index;
            jobs.add(() -> {

                for (int item = i; item < src.length; ++item) {
                    //locks[item].lock();
                    dest[item] += + src[item - i];
                    //locks[item].unlock();
                }
            });
        }

        ExecutorService executor = Executors.newWorkStealingPool();

        start = System.currentTimeMillis();
        jobs.forEach(executor::submit);

        closeExecutor(executor);
        printArray(src);
        printArray(dest);
    }

    private static void printArray(int[] src) {

        for (int i = 0; i < src.length; ++i) {
            System.out.print(src[i] + " ");
        }
        System.out.println();
    }

    private static void randomInit(int[] list) {

        Random random = new Random();

        for (int index = 0; index < SIZE; ++index) {
            list[index] = random.nextInt(BOUND);
        }
    }

    private static void closeExecutor(ExecutorService executor) {

        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
            end = System.currentTimeMillis();
            System.out.println("My task took " + (end - start) + " milliseconds to execute.");
        }
    }
}
