import Model.ComputedValue;
import Model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
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

        List<Supplier<ComputedValue>> jobs = new ArrayList<>();

        for (int row = 0; row < c.size(); ++row) {
            for (int col = 0; col < c.size(); ++col) {
                final int R = row;
                final int C = col;

                jobs.add(() -> {
                    return new ComputedValue(R, C, a.get(R, C) + b.get(R, C));
                });
            }
        }

        start = System.currentTimeMillis();
        jobs.forEach(j -> {
            CompletableFuture.supplyAsync(j)
                    .thenApply(computedValue -> {
                        c.set(computedValue.getRow(), computedValue.getColumn(), computedValue.getValue());
                        return System.currentTimeMillis();
                    })
                    .thenAccept(end -> {
                        System.out.println("My task took " + (end - start) + " milliseconds to execute");
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
