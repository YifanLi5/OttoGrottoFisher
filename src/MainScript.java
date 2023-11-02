import Paint.ScriptPaint;
import Task.Task;
import Task.Drop;
import Task.Fish;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;


@ScriptManifest(author = "PayPalMeRSGP", name = "Otto Grotto1", info = "k", version = 0.12, logo = "")
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
