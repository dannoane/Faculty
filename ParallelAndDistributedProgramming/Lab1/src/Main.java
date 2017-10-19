import Model.Account;
import Repository.Accounts;
import Operation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        Accounts accounts = new Accounts();
        Random random = new Random();

        // Create random accounts
        IntStream
                .range(0, 50)
                .forEach(i -> accounts.addAccount(new Account(random.nextInt(900) + 100)));

        List<Operation> operations = new ArrayList<>();
        // Create random transactions
        IntStream
                .range(0, 25)
                .forEach(i -> {
                    int a, b;
                    a = random.nextInt(accounts.getSize());
                    while ((b = random.nextInt(accounts.getSize())) == a);

                    operations.add(
                            new Transaction(
                                    accounts.getAccount(a),
                                    accounts.getAccount(b),
                                    random.nextInt(90) + 10));
                });
        // Create some checks
        IntStream
                .range(0, 3)
                .forEach(i -> {
                    operations.add(new Check(accounts.getAccounts()));
                });

        // It will use as many threads as CPUs
        ExecutorService executor = Executors.newWorkStealingPool();

        // Add all operations in the executor
        operations.forEach(o -> executor.submit(o.getOperation()));

        // try stop the executor properly if possible
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

        // Run another check after all other operations completed
        Thread thread = new Thread(new Check(accounts.getAccounts()).getOperation());
        thread.start();
    }
}
