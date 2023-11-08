import Paint.ScriptPaint;
import Task.Drop;
import Task.Fish;
import Task.Idle;
import Task.Task;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import static Task.ScriptConstants.sessionMean;
import static Task.ScriptConstants.sessionStdDev;


@ScriptManifest(author = "yfoo", name = "Otto Grotto v0.2.2", info = "k", version = 0.2, logo = "")
public class MainScript extends Script {
    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        new ScriptPaint(this);
        log(String.format("Using Mean: %d, Std_dev: %d for idles.", sessionMean, sessionStdDev));
        new Fish(this.bot);
        new Drop(this.bot);
        new Idle(this.bot);
    }

    @Override
    public int onLoop() throws InterruptedException {
        Task currentTask = Task.pollRunnableTasks();
        if (currentTask != null) {
            currentTask.run();
        }
        return random(1000);
    }
}
