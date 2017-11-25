import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int SIZE = 200;
    private static final int NUMBER_SIZE = 2000;
    private static ExecutorService executorService;

    private static double start;
    private static double end;

    public static void main(String[] args) {

        BigNumber[] numbers = new BigNumber[SIZE];

        randomInit(numbers);

        Channel result = new Channel();

        executorService = Executors.newFixedThreadPool(50000);

        start = System.currentTimeMillis();
        executorService.submit(() -> sum(numbers, 0, numbers.length, result));

        printResult(result);

        closeExecutor(executorService);
    }

    static void sum(BigNumber[] numbers, int left, int right, Channel result) {

        if (right - left == 1) {
            for (int index = 0; index < numbers[left].length(); ++index) {
                try {
                    result.enqueue(numbers[left].getDigit(index));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result.close();
            return;
        }

        int half = (left + right) / 2;

        Channel leftSum = new Channel();
        Channel rightSum = new Channel();

        executorService.submit(() -> sum(numbers, left, half, leftSum));
        executorService.submit(() -> sum(numbers, half, right, rightSum));

        computeSum(leftSum, rightSum, result);
    }

    private static void computeSum(Channel leftSum, Channel rightSum, Channel result) {

        try {
            int carry = 0, sum;

            while (!leftSum.isClosed() && !rightSum.isClosed()) {
                sum = leftSum.dequeue() + rightSum.dequeue() + carry;
                result.enqueue(sum % 10);
                carry = sum / 10;
            }

            while (!leftSum.isClosed()) {
                sum = leftSum.dequeue() + carry;
                result.enqueue(sum % 10);
                carry = sum / 10;
            }

            while (!rightSum.isClosed()) {
                sum = rightSum.dequeue() + carry;
                result.enqueue(sum % 10);
                carry = sum / 10;
            }

            if (carry > 0) {
                result.enqueue(carry);
            }

            result.close();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printResult(Channel result) {

        Stack<Integer> number = new Stack<>();

        while (!result.isClosed()) {
            try {
                number.push(result.dequeue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (!number.isEmpty()) {
            System.out.print(number.pop());
        }
    }

    static void randomInit(BigNumber[] numbers) {

        Random random = new Random();

        for (int i = 0; i < SIZE; ++i) {
            numbers[i] = new BigNumber();
            for (int j = 0; j < NUMBER_SIZE; ++j) {
                numbers[i].setDigit(j, random.nextInt(10));
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
