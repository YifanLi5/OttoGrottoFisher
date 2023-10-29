import Paint.ScriptPaint;
import Task.Task;
import Task.Drop;
import Task.Fish;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.util.PriorityQueue;

@ScriptManifest(author = "PayPalMeRSGP", name = "Barbarian_Fisher v2.0", info = "Barbarian Fisher used to demo PrioritizedReactiveTask", version = 2.0, logo = "")
public class MainScript extends Script{
    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        new ScriptPaint(this);

        new Fish(this.bot);
        new Drop(this.bot);
    }

    @Override
    public int onLoop() throws InterruptedException {
        Task currentTask = Task.pollRunnableTasks();
        if(currentTask != null) {
            currentTask.run();
        }
        return random(1000);
    }
}
