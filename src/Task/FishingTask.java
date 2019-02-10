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
        if(!(inventory.contains("Feather") || inventory.contains("Bait")) || !inventory.contains("Barbarian rod")) {
            warn("Inventory does not contain feathers or bait");
            bot.getScriptExecutor().stop(false);
        }

        final NPC[] fishingSpot = new NPC[1];
        boolean fishingSpotExists = new ConditionalSleep(2000) {
            @Override
            public boolean condition() throws InterruptedException {
                fishingSpot[0] = npcs.closest("Fishing spot");
                return fishingSpot[0] != null;
            }
        }.sleep();

        if(fishingSpotExists && runTaskThread.get()) {
            if(fishingSpot[0].exists() && runTaskThread.get()) {
                if(fishingSpot[0].interact("Use-rod") && runTaskThread.get())
                new ConditionalSleep(5000, 1000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return myPlayer().getAnimation() == 623;
                    }
                }.sleep();

                mouse.moveOutsideScreen();
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
