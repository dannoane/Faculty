import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static final int SIZE = 1000;
    private static final int BOUND = 5;

    private static double start;
    private static double end;

    public static void main(String[] args) {

        Vector src = new Vector(SIZE);
        Vector dest = new Vector(SIZE);

        randomInit(src);

        start = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            try {
                partialSum(src, 0, src.length(), dest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        end = System.currentTimeMillis();
        System.out.println("My task took " + (end - start) + " milliseconds to execute.");

        printArray(src);
        printArray(dest);
    }

    private static void partialSum(Vector src, int left, int right, Vector dest) throws InterruptedException {

        if (right - left == 1) {

            if (left == 0) {
                dest.set(left, src.get(left));
            }
            else {
                dest.set(left, dest.get(left - 1) + src.get(left));
            }

            return;
        }

        int half = (left + right) / 2;

        Thread leftThread = new Thread(() -> {
            try {
                partialSum(src, left, half, dest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread rightThread = new Thread(() -> {
            try {
                partialSum(src, half, right, dest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        leftThread.start();
        rightThread.start();

        leftThread.join();
        rightThread.join();
    }

    private static void printArray(Vector src) {

        for (int i = 0; i < src.length(); ++i) {
            try {
                System.out.print(src.get(i) + " ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    private static void randomInit(Vector vector) {

        Random random = new Random();

        for (int index = 0; index < SIZE; ++index) {
            try {
                vector.set(index, random.nextInt(BOUND));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeExecutor(ExecutorService executor) {

        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
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
