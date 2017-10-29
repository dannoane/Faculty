import Model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Main {

    static private long start;

    public static void main(String[] args) {

        Random random = new Random();

        int rows = 100;//random.nextInt(100) + 1;
        int cols = 100;//random.nextInt(100) + 1;
        Matrix a = new Matrix(rows, cols);
        Matrix b = new Matrix(rows, cols);
        Matrix c = new Matrix(rows, cols);

        randomInit(a);
        randomInit(b);

        List<Supplier<Long>> jobs = new ArrayList<>();
        int tasks = random.nextInt(200) + 1;

        for (int counter = 0; counter < tasks; ++counter) {
            int start = counter * (c.size() / tasks);
            int end = (counter + 1) * (c.size() / tasks) + (counter + 1 == tasks ? c.size() % tasks : 0);

            jobs.add(() -> {
                for (int index = start; index < end; ++index) {
                    int row = index / c.getCols();
                    int col = index % c.getCols();

                    c.set(row, col, a.get(row, col) + b.get(row, col));
                }

                return System.currentTimeMillis();
            });
        }

        start = System.currentTimeMillis();
        jobs.forEach(j -> {
            CompletableFuture.supplyAsync(j)
                    .thenAccept((end) -> {
                        System.out.println("My task took " + (end - start) + " milliseconds to execute using " + tasks + " threads.");
                    });
        });
    }

    private static void randomInit(Matrix a) {

        Random random = new Random();

        for (int row = 0; row < a.getRows(); ++row) {
            for (int col = 0; col < a.getCols(); ++col) {
                a.set(row, col, random.nextInt(20));
            }
        }
    }
}
