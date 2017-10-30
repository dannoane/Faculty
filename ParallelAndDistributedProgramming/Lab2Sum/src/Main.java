import Model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        Random random = new Random();

        int rows = 100;//random.nextInt(100) + 1;
        int cols = 100;//random.nextInt(100) + 1;
        Matrix a = new Matrix(rows, cols);
        Matrix b = new Matrix(rows, cols);
        Matrix c = new Matrix(rows, cols);

        randomInit(a);
        randomInit(b);

        List<Thread> jobs = new ArrayList<>();

        for (int threads = 50; threads <= 400; threads += 50) {
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

            long start = System.currentTimeMillis();

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
            jobs.clear();
        }

        //System.out.println(a);
        //System.out.println(b);
        //System.out.println(c);
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
