import Model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static long start;
    private static long end;

    public static void main(String[] args) {

        int n, m, p, q;
        n = 2; m = 2; p = 2; q = 2;

        Matrix a, b, ab, c, abc;
        a = new Matrix(n, m);
        b = new Matrix(m, p);
        ab = new Matrix(n, p);      // the result of a * b
        c = new Matrix(p, q);
        abc = new Matrix(n, q);     // the result of (a * b) * c

        try {
            randomInit(a);
            randomInit(b);
            randomInit(c);
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        ExecutorService firstExecutor = Executors.newWorkStealingPool();
        ExecutorService secondExecutor = Executors.newWorkStealingPool();
        int firstThreads = 100, secondThreads = 200;
        List<Runnable> firstProd = new ArrayList<>();
        List<Runnable> secondProd = new ArrayList<>();

        addJobs(firstThreads, firstProd, a, b, ab);
        addJobs(secondThreads, secondProd, ab, c, abc);

        start = System.currentTimeMillis();
        firstProd.forEach(firstExecutor::submit);
        secondProd.forEach(secondExecutor::submit);

        closeExecutor(firstExecutor);
        closeExecutor(secondExecutor);

        System.out.println(a.toString());
        System.out.println(b.toString());
        System.out.println(c.toString());
        System.out.println(abc.toString());
    }

    private static void addJobs(int threads, List<Runnable> jobs, Matrix first, Matrix second, Matrix dest) {

        for (int counter = 0; counter < threads; ++counter) {
            int start = counter * (dest.size() / threads);
            int end = (counter + 1) * (dest.size() / threads) + (counter + 1 == threads ? dest.size() % threads : 0);

            jobs.add(() -> {
                try {
                    for (int index = start; index < end; ++index) {
                        int row = index / dest.getCols();
                        int col = index % dest.getCols();

                        int value = 0;
                        for (int x = 0; x < first.getCols(); ++x) {
                            value += first.get(row, x) * second.get(x, col);
                        }
                        dest.set(row, col, value);
                    }
                }
                catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            });
        }
    }

    private static void randomInit(Matrix a) throws InterruptedException {

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
            System.out.println("My task took " + (end - start) + " milliseconds to execute");
        }
    }
}
