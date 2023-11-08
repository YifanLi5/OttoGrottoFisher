package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

import java.util.ArrayList;
import java.util.Random;

public abstract class Task extends MethodProvider {
    static ArrayList<Task> subclassInstances = new ArrayList<>();
    static final int IDLE_ID = -1;

    public Task(Bot bot) {
        exchangeContext(bot);
        subclassInstances.add(this);
    }

    abstract boolean shouldRun();

    public abstract void run() throws InterruptedException;

    int probabilityWeight() {
        return 1;
    }

    int randomGaussian(double mean, double stddev) {
        return (int) Math.abs((new Random().nextGaussian() * stddev + mean));
    }


    public static Task pollRunnableTasks() {
        int weightingSum = 0;
        ArrayList<Task> runnableTasks = new ArrayList<>();
        for (Task task : Task.subclassInstances) {
            if (task.shouldRun()) {
                runnableTasks.add(task);
                weightingSum += task.probabilityWeight();
            }
        }
        if (runnableTasks.isEmpty()) {
            return null;
        } else if (runnableTasks.size() == 1) {
            return runnableTasks.get(0);
        }

        int roll = random(weightingSum);
        int idx = 0;
        for (; idx < runnableTasks.size(); idx++) {
            roll -= runnableTasks.get(idx).probabilityWeight();
            if (roll < 0) {
                break;
            }
        }

        return runnableTasks.get(idx);
    }
}
