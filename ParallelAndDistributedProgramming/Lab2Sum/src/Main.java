import Model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        Random random = new Random();

        int rows = random.nextInt(100) + 1;
        int cols = random.nextInt(100) + 1;
        Matrix a = new Matrix(rows, cols);
        Matrix b = new Matrix(rows, cols);
        Matrix c = new Matrix(rows, cols);

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

                    c.set(row, col, a.get(row, col) + b.get(row, col));
                }
            }));
        }

        //jobs.forEach(j -> executor.submit(j));
        jobs.forEach(j -> j.run());
        jobs.forEach(j -> {
            try {
                j.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        //closeExecutor(executor);

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
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
        }
    }
}
