import Model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        Random random = new Random();

        int n = 100;//random.nextInt(99) + 1;
        int m = 150;//random.nextInt(99) + 1;
        int p = 200;//random.nextInt(99) + 1;
        Matrix a = new Matrix(n, m);
        Matrix b = new Matrix(m, p);
        Matrix c = new Matrix(n, p);

        randomInit(a);
        randomInit(b);

        //ExecutorService executor = Executors.newWorkStealingPool();
        //List<Runnable> jobs = new ArrayList<>();
        List<Thread> jobs = new ArrayList<>();
        int threads = random.nextInt(c.size()) + 1;

        for (int counter  = 0; counter  < threads; ++counter) {
            int start = counter * (c.size() / threads);
            int end = (counter + 1) * (c.size() / threads) + (counter + 1 == threads ? c.size() % threads : 0);

            jobs.add(new Thread(() -> {
                for (int index = start; index < end; ++index) {
                    int row = index / c.getCols();
                    int col = index % c.getCols();

                    c.set(row, col, 0);
                    for (int x = 0; x < a.getCols(); ++x) {
                        c.set(row, col, c.get(row, col) + (a.get(row, x) * b.get(x, col)));
                    }
                }
            }));
        }

        long start = System.currentTimeMillis();

        //jobs.forEach(j -> executor.submit(j));
        jobs.forEach(j -> j.run());
        jobs.forEach(j -> {
            try {
                j.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("My task took " + (end - start) + " milliseconds to execute using " + threads + " threads.");

        //closeExecutor(executor);

        //System.out.println(a);
        //System.out.println(b);
        //System.out.println(c);
    }

    private static void randomInit(Matrix a) {

        Random random = new Random();

        for (int row = 0; row < a.getRows(); ++row) {
            for (int col = 0; col < a.getCols(); ++col) {
                a.set(row, col, random.nextInt(5));
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
        }
    }
}
