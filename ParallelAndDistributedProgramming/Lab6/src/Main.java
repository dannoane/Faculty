import Model.Polynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    private static final int SIZE = 1024;
    private static final int CONSTANT_BOUND = 1000;

    public static void main(String[] args) {

        //sequentialRegular();
        //parallelRegular();

        //karatsubaSequentail();
        karatsubaParallel();
    }

    private static void karatsubaParallel() {

        Polynomial a = new Polynomial(SIZE);
        Polynomial b = new Polynomial(SIZE);
        Polynomial c;

        randomInit(a);
        randomInit(b);

        double start = System.currentTimeMillis();
        c = karatsubaMultiplyParallel(a, b);
        double end = System.currentTimeMillis();
        System.out.println("My task took " + (end - start) + " milliseconds to execute");
    }

    private static Polynomial karatsubaMultiplyParallel(Polynomial a, Polynomial b) {

        if (a.size() != b.size()) {
            return new Polynomial(0);
        }

        return new Polynomial(karatsubaRecursiveParallel(a, b));
    }

    private static int[] karatsubaRecursiveParallel(Polynomial a, Polynomial b) {

        int[] result = new int[a.size() * 2];
        System.out.println(a.size());
        if (a.size() == 1) {
            result[0] = a.get(0) * b.get(0);
        }
        else {
            int half = a.size() / 2;

            int[] aHigh = new int[half];
            int[] aLow = new int[half];
            int[] bHigh = new int[half];
            int[] bLow = new int[half];

            int[] aLowHigh = new int[half];
            int[] bLowHigh = new int[half];

            for (int index = 0; index < half; ++index) {
                aHigh[index] = a.get(half + index);
                aLow[index] = a.get(index);
                aLowHigh[index] = aLow[index] + aHigh[index];

                bHigh[index] = b.get(half + index);
                bLow[index] = b.get(index);
                bLowHigh[index] = bLow[index] + bHigh[index];
            }

            int[] resultLow = new int[0], resultHigh = new int[0], resultLowHigh = new int[0];
            try {
                resultLow = CompletableFuture.supplyAsync(() -> { return karatsubaRecursive(new Polynomial(aLow), new Polynomial(bLow)); })
                        .get();
                resultHigh = CompletableFuture.supplyAsync(() -> { return karatsubaRecursive(new Polynomial(aHigh), new Polynomial(bHigh)); })
                        .get();

                resultLowHigh = CompletableFuture.supplyAsync(() -> { return karatsubaRecursive(new Polynomial(aLowHigh), new Polynomial(bLowHigh)); })
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


            int[] resultMiddle = new int[a.size()];
            for (int index = 0; index < a.size(); ++index) {
                resultMiddle[index] = resultLowHigh[index] - resultLow[index] - resultHigh[index];
            }

            for (int index = 0, middleOffset = a.size() / 2; index < a.size(); ++index) {
                result[index] += resultLow[index];
                result[index + a.size()] += resultHigh[index];
                result[index + middleOffset] += resultMiddle[index];
            }
        }

        return result;
    }

    private static void karatsubaSequentail() {

        Polynomial a = new Polynomial(SIZE);
        Polynomial b = new Polynomial(SIZE);
        Polynomial c;

        randomInit(a);
        randomInit(b);

        double start = System.currentTimeMillis();
        c = karatsubaMultiply(a, b);
        double end = System.currentTimeMillis();
        System.out.println("My task took " + (end - start) + " milliseconds to execute");
    }

    private static Polynomial karatsubaMultiply(Polynomial a, Polynomial b) {
        
        if (a.size() != b.size()) {
            return new Polynomial(0);
        }
        
        return new Polynomial(karatsubaRecursive(a, b));
    }

    private static int[] karatsubaRecursive(Polynomial a, Polynomial b) {

        int[] result = new int[a.size() * 2];
        System.out.println(a.size());
        if (a.size() == 1) {
            result[0] = a.get(0) * b.get(0);
        }
        else {
            int half = a.size() / 2;

            int[] aHigh = new int[half];
            int[] aLow = new int[half];
            int[] bHigh = new int[half];
            int[] bLow = new int[half];

            int[] aLowHigh = new int[half];
            int[] bLowHigh = new int[half];

            for (int index = 0; index < half; ++index) {
                aHigh[index] = a.get(half + index);
                aLow[index] = a.get(index);
                aLowHigh[index] = aLow[index] + aHigh[index];

                bHigh[index] = b.get(half + index);
                bLow[index] = b.get(index);
                bLowHigh[index] = bLow[index] + bHigh[index];
            }

            int[] resultLow = karatsubaRecursive(new Polynomial(aLow), new Polynomial(bLow));
            int[] resultHigh = karatsubaRecursive(new Polynomial(aHigh), new Polynomial(bHigh));

            int[] resultLowHigh = karatsubaRecursive(new Polynomial(aLowHigh), new Polynomial(bLowHigh));

            int[] resultMiddle = new int[a.size()];
            for (int index = 0; index < a.size(); ++index) {
                resultMiddle[index] = resultLowHigh[index] - resultLow[index] - resultHigh[index];
            }

            for (int index = 0, middleOffset = a.size() / 2; index < a.size(); ++index) {
                result[index] += resultLow[index];
                result[index + a.size()] += resultHigh[index];
                result[index + middleOffset] += resultMiddle[index];
            }
        }

        return result;
    }

    private static void parallelRegular() {

        Polynomial a = new Polynomial(SIZE);
        Polynomial b = new Polynomial(SIZE);
        Polynomial c = new Polynomial(SIZE * 2);

        randomInit(a);
        randomInit(b);

        List<Runnable> jobs = new ArrayList<>();
        ExecutorService executor = Executors.newWorkStealingPool();

        int threads = SIZE / 10;
        for (int counter  = 0; counter < threads; ++counter) {
            int start = counter * (a.size() / threads);
            int end = (counter + 1) * (a.size() / threads) + (counter + 1 == threads ? a.size() % threads : 0);

            jobs.add(() -> {
                for (int indexA = start; indexA < end; ++indexA) {
                    for (int indexB = 0; indexB < b.size(); ++indexB) {
                        c.set(indexA + indexB, c.get(indexA + indexB) + (a.get(indexA) * b.get(indexB)));
                    }
                }
            });
        }

        float start = System.currentTimeMillis();
        jobs.forEach(executor::submit);
        closeExecutor(executor, start);
    }

    private static void closeExecutor(ExecutorService executor, float start) {

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
            float end = System.currentTimeMillis();
            System.out.println("My task took " + (end - start) + " milliseconds to execute.");
        }
    }

    private static void sequentialRegular() {

        Polynomial a = new Polynomial(SIZE);
        Polynomial b = new Polynomial(SIZE);
        Polynomial c = new Polynomial(SIZE * SIZE);

        randomInit(a);
        randomInit(b);

        double start = System.currentTimeMillis();
        for (int indexA = 0; indexA < a.size(); ++indexA) {
            for (int indexB = 0; indexB < b.size(); ++indexB) {
                c.set(indexA + indexB, c.get(indexA + indexB) + (a.get(indexA) * b.get(indexB)));
            }
        }
        double end = System.currentTimeMillis();
        System.out.println("My task took " + (end - start) + " milliseconds to execute");
    }

    private static void randomInit(Polynomial p) {

        Random random = new Random();

        for (int index = 0; index < p.size(); ++index) {
            p.set(index, random.nextInt(CONSTANT_BOUND));
        }
    }
}
