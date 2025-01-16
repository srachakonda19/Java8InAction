import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Singleton class for TaskExecutor
class TaskExecutor {
    private static TaskExecutor instance;
    private final ThreadPoolExecutor executor;
    private final AtomicInteger completedTasks;
    private final AtomicInteger failedTasks;

    // Private constructor for Singleton
    private TaskExecutor(int poolSize) {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        this.completedTasks = new AtomicInteger(0);
        this.failedTasks = new AtomicInteger(0);
    }

    // Thread-safe Singleton instance getter
    public static synchronized TaskExecutor getInstance(int poolSize) {
        if (instance == null) {
            instance = new TaskExecutor(poolSize);
        }
        return instance;
    }

    // Method to submit tasks
    public void submitTask(Runnable task) {
        executor.submit(() -> {
            try {
                task.run();
                completedTasks.incrementAndGet(); // Increment completed tasks
            } catch (Exception e) {
                failedTasks.incrementAndGet(); // Increment failed tasks
                System.err.println("Task failed: " + e.getMessage());
            }
        });
    }

    // Method to get completed task count
    public int getCompletedTaskCount() {
        return completedTasks.get();
    }

    // Method to get failed task count
    public int getFailedTaskCount() {
        return failedTasks.get();
    }

    // Shutdown the executor
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}

// Example usage
public class MultiThreadedTaskExecutorExample {
    public static void main(String[] args) {
        TaskExecutor taskExecutor = TaskExecutor.getInstance(5);

        // Submit tasks
        for (int i = 0; i < 10; i++) {
            int taskId = i + 1;
            taskExecutor.submitTask(() -> {
                System.out.println("Executing task " + taskId);
                if (taskId % 2 == 0) {
                    throw new RuntimeException("Intentional failure for task " + taskId);
                }
            });
        }

        // Allow tasks to complete
        taskExecutor.shutdown();

        // Print task statistics
        System.out.println("Completed Tasks: " + taskExecutor.getCompletedTaskCount());
        System.out.println("Failed Tasks: " + taskExecutor.getFailedTaskCount());
    }
}
