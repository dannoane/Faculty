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

        IntStream
                .range(0, 50)
                .forEach(i -> accounts.addAccount(new Account(random.nextInt(900) + 100)));

        ExecutorService executor = Executors.newWorkStealingPool();

        List<Operation> operations = new ArrayList<>();
        IntStream
                .range(0, 3)
                .forEach(i -> {
                    operations.add(
                            new Transaction(
                                    accounts.getAccount(1),
                                    accounts.getAccount(2),
                                    random.nextInt(90) + 10));
                });
        IntStream
                .range(0, 3)
                .forEach(i -> {
                    operations.add(new Check(accounts.getAccounts()));
                });

        operations.forEach(o -> executor.submit(o.getOperation()));

        executor.submit(new Check(accounts.getAccounts()).getOperation());

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
