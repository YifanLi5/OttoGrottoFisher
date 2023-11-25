import Paint.ScriptPaint;
import Task.Drop;
import Task.Fish;
import Task.Idle;
import Task.Task;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import static Task.ScriptConstants.*;

@ScriptManifest(author = "yfoo", name = "Otto Grotto", info = "Barbarian Fishing Script", version = 1.0, logo = "")
public class MainScript extends Script {
    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        new ScriptPaint(this);
        log(String.format("Using Mean: %dms, Std_dev: %dms for idles. Shift drop miss rate: %d/1000", SESSION_MEAN, SESSION_STD_DEV, SESSION_DROP_SKIP));
        new Fish(this.bot);
        new Drop(this.bot);
        new Idle(this.bot);

        camera.movePitch(67);
    }

    @Override
    public int onLoop() throws InterruptedException {
        Task currentTask = Task.pollRunnableTasks();
        if (currentTask != null) {
            currentTask.runTask();
        }
        return random(1000);
    }
}
