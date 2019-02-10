import Paint.MyPainter;
import Task.DropTask;
import Task.FishingTask;
import Task.PrioritizedReactiveTask;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.util.PriorityQueue;

@ScriptManifest(author = "PayPalMeRSGP", name = MainScript.SCRIPT_NAME, info = "Barbarian Fisher used to demo PrioritizedReactiveTask", version = 0.5, logo = "")
public class MainScript extends Script{
    private long mainThreadID = Thread.currentThread().getId();
    static final String SCRIPT_NAME = "Barbarian_Fisher v0.99";
    private PriorityQueue<PrioritizedReactiveTask> taskQ;

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        taskQ = PrioritizedReactiveTask.initializeTaskQueue();
        new MyPainter(this);
        new FishingTask(bot).startCheckEnqueueTaskConditionThread();
        new DropTask(bot).startCheckEnqueueTaskConditionThread();
    }

    @Override
    public int onLoop() throws InterruptedException {
        if(!taskQ.isEmpty()) {
            PrioritizedReactiveTask currentTask = taskQ.poll();
            currentTask.setTaskEnqueuedToFalse();
            currentTask.startTaskRunnerThread();
            log("Thread " + mainThreadID + " awaiting task " + currentTask.getClass().getSimpleName() + " to finish.");
            while(currentTask.isTaskRunning()) {
                PrioritizedReactiveTask peeked = taskQ.peek();
                //interrupt the current task if one of greater priority is enqueued
                if(peeked != null && peeked.getPriority().getValue() > currentTask.getPriority().getValue()) {
                    currentTask.stopTask();
                }
            }
            log("Finished task: " + currentTask.getClass().getSimpleName());
        }

        return 1000;
    }

    @Override
    public void onExit() throws InterruptedException {
        super.onExit();
        PrioritizedReactiveTask.onStopCleanUp();
    }
}
