import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Channel {

    private Queue<Integer> data;
    private boolean closed;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    Channel() {
        this.data = new ArrayDeque<>();
        this.closed = false;
    }

    public boolean isClosed() {

        lock.lock();

        try {
            return closed && data.isEmpty();
        }
        finally {
            lock.unlock();
        }
    }

    public void close() {

        lock.lock();
        this.closed = true;
        lock.unlock();
    }

    void enqueue(int value) throws InterruptedException {

        lock.lock();

        try {
            data.add(value);
            notEmpty.signal();
        }
        finally {
            lock.unlock();
        }
    }

    int dequeue() throws InterruptedException {

        lock.lock();

        try {
            while (data.size() == 0) {
                notEmpty.await();
            }

            return data.remove();
        }
        finally {
            lock.unlock();
        }
    }
}
