package Operation;

import Model.Account;
import Model.Log;

public class Transaction extends Operation {

    private Account from, to;
    private int amount;

    public Transaction(Account from, Account to, int amount) {

        this.from = from;
        this.to = to;
        this.amount = amount;

        this.operation = () -> {

            this.lockAccounts();
            this.transfer();
            this.unlockAccounts();
        };
    }

    private void lockAccounts() {

        // lock in the same order: smaller id first
        if (from.getId() < to.getId()) {
            System.out.println("Trying to lock accounts " + from.getId() + " " + to.getId());
            from.getLock().lock();
            to.getLock().lock();
        }
        else {
            System.out.println("Trying to lock accounts " + to.getId() + " " + from.getId());
            to.getLock().lock();
            from.getLock().lock();
        }
    }

    private void unlockAccounts() {

        System.out.println("Trying to unlock accounts " + from.getId() + " " + to.getId());
        from.getLock().unlock();
        to.getLock().unlock();
    }

    private void transfer() {

        from.setBalance(-amount);
        to.setBalance(amount);
        addLogs();

        System.out.println("FROM " + from.getId() + " TO " + to.getId() + " AMOUNT " + amount);
    }

    private void addLogs() {

        Log log = new Log(from.getId(), to.getId(), amount);
        from.addLog(log);
        to.addLog(log);
    }
}
