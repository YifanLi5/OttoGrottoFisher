package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PrioritizedReactiveTask extends MethodProvider implements Comparable<PrioritizedReactiveTask> {

    public enum Priority {
        HIGH(1), LOW(0);
        private final int value;
        Priority(final int newValue) {
            value = newValue;
        }
        public int getValue() { return value; }
    }


    Priority p;
    private static PriorityQueue<PrioritizedReactiveTask> taskQueue;
    private static Set<PrioritizedReactiveTask> taskHistory; //used to kill all threads

    AtomicBoolean runEnqueueTaskThread;
    AtomicBoolean runTaskThread = new AtomicBoolean(false);

    public PrioritizedReactiveTask(Bot bot) {
        exchangeContext(bot);
    }

    public static PriorityQueue<PrioritizedReactiveTask> initializeTaskQueue() {
        if(taskQueue == null) {
            taskQueue = new PriorityQueue<>();
            taskHistory = new HashSet<>();
        }
        return taskQueue;
    }

    public static void killAllThreads() {
        for(PrioritizedReactiveTask task: taskHistory) {
            task.stopTask();
            task.stopCheckEnqueueTaskConditionThread();
        }
    }

    public static void nullifyTaskQueue() {
        taskQueue = null;
    }

    public void startCheckEnqueueTaskConditionThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runEnqueueTaskThread = new AtomicBoolean(true);
                while(runEnqueueTaskThread.get()) {
                    if(taskQueue.contains(PrioritizedReactiveTask.this)) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    if(checkEnqueueTaskCondition()) {
                        System.out.println("Thread " + Thread.currentThread().getId() + " enqueued task: " + this.getClass().getSimpleName());
                        taskQueue.add(PrioritizedReactiveTask.this);
                        taskHistory.add(PrioritizedReactiveTask.this);
                    }
                }
            }
        }).start();
    }

    public void stopCheckEnqueueTaskConditionThread() {
        if(runEnqueueTaskThread != null)
            runEnqueueTaskThread.set(false);
    }

    public void runTaskAsync() {
        runTaskThread.set(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    task();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runTaskThread.set(false);
                }
            }
        }).start();
    }

    public void stopTask() {
        if(runTaskThread != null)
            runTaskThread.set(false);
    }

    abstract void task() throws InterruptedException;

    abstract boolean checkEnqueueTaskCondition();

    @Override
    public int compareTo(PrioritizedReactiveTask other) {
        return other.p.getValue() - this.p.getValue();
    }

    long randomNormalDist(double mean, double stddev){
        return (long) Math.abs((new Random().nextGaussian() * stddev + mean));
    }

    public boolean isTaskRunning() {
        return runTaskThread.get();
    }

    public Priority getPriority() {
        return p;
    }
}