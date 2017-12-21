import Model.Image;
import Model.MotionBlur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final int gridSize = 100;

    public static void main(String[] args) {

        String filename = "test.png";
        Image image = new Image(filename);
        image.read();

        List<Runnable> jobs = new ArrayList<>();
        ExecutorService executor = Executors.newWorkStealingPool();

        for (int i = 0; i < image.width(); i += gridSize) {
            for (int j = 0; j < image.height(); j += gridSize) {
                final int x, y;
                x = i;
                y = j;


                jobs.add(() -> {
                   MotionBlur.apply(x, y, gridSize, image);
                });
            }
        }

        jobs.forEach(executor::submit);
        closeExecutor(executor);

        image.write();
    }

    private static void closeExecutor(ExecutorService executor) {

        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(20, TimeUnit.SECONDS);
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
