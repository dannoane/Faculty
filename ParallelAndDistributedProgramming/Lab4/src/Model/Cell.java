package Model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {

    private Integer value;
    private final Lock lock = new ReentrantLock();
    private final Condition notComputed = lock.newCondition();

    public Cell() {}

    public void set(Integer value) throws InterruptedException {

        lock.lock();

        try {
            this.value = value;
            notComputed.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public Integer get() throws InterruptedException {

        lock.lock();

        try {
            while (value == null) {
                notComputed.await();
            }

            return value;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Cell{" +
                "value=" + value +
                '}';
    }
}
