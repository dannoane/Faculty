import Model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    static private long start;
    static private long end;
    static private int threads;

    public static void main(String[] args) {

        Random random = new Random();

        int rows = 100;//random.nextInt(100) + 1;
        int cols = 100;//random.nextInt(100) + 1;
        Matrix a = new Matrix(rows, cols);
        Matrix b = new Matrix(rows, cols);
        Matrix c = new Matrix(rows, cols);

        randomInit(a);
        randomInit(b);

        List<Runnable> jobs = new ArrayList<>();

        for (threads = 50; threads <= 400; threads += 50) {
            ExecutorService executor = Executors.newWorkStealingPool();

            for (int counter  = 0; counter  < threads; ++counter) {
                int start = counter * (c.size() / threads);
                int end = (counter + 1) * (c.size() / threads) + (counter + 1 == threads ? c.size() % threads : 0);

                jobs.add(() -> {
                    for (int index = start; index < end; ++index) {
                        int row = index / c.getCols();
                        int col = index % c.getCols();

                        c.set(row, col, a.get(row, col) + b.get(row, col));
                    }
                });
            }

            start = System.currentTimeMillis();
            jobs.forEach(j -> executor.submit(j));
            closeExecutor(executor);
            jobs.clear();
        }
    }

    private static void randomInit(Matrix a) {

        Random random = new Random();

        for (int row = 0; row < a.getRows(); ++row) {
            for (int col = 0; col < a.getCols(); ++col) {
                a.set(row, col, random.nextInt(20));
            }
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
            System.out.println("My task took " + (end - start) + " milliseconds to execute using " + threads + " threads.");
        }
    }
}
