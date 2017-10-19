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

        int n, m, p;
        n = random.nextInt(3) + 1;
        m = n;
        p = random.nextInt(3) + 1;

        int[][] matrixA = new int[n][m];
        int[][] matrixB = new int[m][p];
        int[][] matrixC = new int[n][p];
        int dimC = n * p;

        IntStream.range(0, n)
                .forEach(i -> {
                    IntStream.range(0, m)
                            .forEach(j -> matrixA[i][j] = random.nextInt(10));
                });

        IntStream.range(0, m)
                .forEach(i -> {
                    IntStream.range(0, p)
                            .forEach(j -> matrixB[i][j] = random.nextInt(10));
                });

        int nThreads = 3;
        List<Runnable> threads = new ArrayList<>();
        for (int i = 0; i < nThreads; ++i) {
            int start = i * (dimC / nThreads);
            int stop = (i + 1) * (dimC / nThreads) + (i == nThreads - 1 ? dimC % nThreads : 0);
            System.out.println("Start: " + start + " Stop: " + stop);
            threads.add(() -> {
                for (int c = start; c < stop; ++c) {
                    int row = c / p;
                    int col = c % p;

                    matrixC[row][col] = 0;
                    for (int x = 0; x < m; ++x) {
                        matrixC[row][col] += (matrixA[row][x] * matrixB[x][col]);
                    }
                }
            });
        }

        ExecutorService executor = Executors.newWorkStealingPool();
        threads.forEach(t -> executor.submit(t));

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

        printMatrix(matrixA);
        printMatrix(matrixB);
        printMatrix(matrixC);
    }

    private static void printMatrix(int[][] matrix) {

        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                System.out.print(matrix[i][j] + " ");
            }

            System.out.println();
        }

        System.out.println();
    }
}
