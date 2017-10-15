package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private int id;
    private int balance;
    private int initialBalance;
    private List<Log> logs;
    private ReentrantLock lock;

    public Account(int balance) {

        this.balance = balance;
        this.initialBalance = balance;
        this.logs = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public int getInitialBalance() {
        return initialBalance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBalance(int amount) {
        balance += amount;
    }

    public void addLog(Log log) {
        logs.add(log);
    }
}
