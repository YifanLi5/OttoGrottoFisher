package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.utility.ConditionalSleep;

public class FishingTask extends PrioritizedReactiveTask {

    //animations...
    //isFishing = 623
    //idle = -1

    public FishingTask(Bot bot) {
        super(bot);
        this.p = Priority.LOW;
    }

    @Override
    void task() throws InterruptedException {
        log("Thread " + Thread.currentThread().getId() + " is running fishing task");
        final NPC[] fishingSpot = new NPC[1];
        boolean fishingSpotExists = new ConditionalSleep(2000) {
            @Override
            public boolean condition() throws InterruptedException {
                fishingSpot[0] = npcs.closest("Fishing spot");
                return fishingSpot[0] != null;
            }
        }.sleep();

        //runTaskThread.get() evaluates to false???
        if(fishingSpotExists && runTaskThread.get()) {
            long idleTime = randomNormalDist(5000, 3000);
            log("Fishing spot found!, Idling for " + idleTime);
            sleep(idleTime);

            if(fishingSpot[0].exists()) {
                fishingSpot[0].interact("Use-rod");
            }
        } else {
            warn("Fishing spot not found! Stopping!");
            bot.getScriptExecutor().stop(false);
        }
    }

    @Override
    boolean checkEnqueueTaskCondition() {
        return myPlayer().getAnimation() == -1 && !inventory.isFull();
    }
}
