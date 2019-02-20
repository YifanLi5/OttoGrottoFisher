package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.utility.ConditionalSleep;

public class FishingTask extends PrioritizedReactiveTask {

    private static final int FISHING_ANIM_ID = 623;
    private static final int THROW_ROD_ANIM_ID = 622;
    private static final int IDLE_ID = -1;

    private static final Area MID_FISH_AREA = new Area(2498, 3506, 2499, 3513);
    private static final Area N_FISH_AREA = new Area(2502, 3518, 2504, 3517);
    private static final Area S_FISH_AREA = new Area(2502, 3494, 2503, 3498);

    public FishingTask(Bot bot) {
        super(bot);
        this.p = Priority.LOW;
    }

    @Override
    void task() throws InterruptedException {
        log("Thread " + Thread.currentThread().getId() + " is running fishing task");
        tabs.open(Tab.INVENTORY);
        if(!(inventory.contains("Feather") || inventory.contains("Bait")) || !inventory.contains("Barbarian rod")) {
            warn("Inventory does not contain feathers or bait");
            bot.getScriptExecutor().stop(false);
        }

        if(inventory.contains("Leaping trout", "Leaping salmon", "Leaping sturgeon")) {
            long idleTime = randomNormalDist(8000, 3000);
            log("Before re-fishing, Idling for " + idleTime);
            sleep(idleTime);
        }

        if(inventory.isItemSelected()) {
            inventory.deselectItem();
        }

        final NPC[] fishingSpot = new NPC[1];
        boolean fishingSpotExists = new ConditionalSleep(2000) {
            @Override
            public boolean condition() throws InterruptedException {
                fishingSpot[0] = npcs.closest("Fishing spot");
                return fishingSpot[0] != null;
            }
        }.sleep();


        if(fishingSpotExists) {
            if (fishingSpot[0].exists()) {
                if (fishingSpot[0].interact("Use-rod") && runTaskThread.get()) {
                    mouse.moveOutsideScreen();
                    new ConditionalSleep(5000, 1000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return myPlayer().getAnimation() == FISHING_ANIM_ID || myPlayer().getAnimation() == THROW_ROD_ANIM_ID;
                        }
                    }.sleep();
                }
            }
        } else {
            warn("Fishing spot not found! Attempt to move to another spot then resuming search");
            if(!MID_FISH_AREA.contains(myPosition())) {
                if(walking.walk(MID_FISH_AREA)) {
                    task();
                }
            } else if(!S_FISH_AREA.contains(myPosition())) {
                if(walking.walk(S_FISH_AREA)) {
                    task();
                }
            } else if(!N_FISH_AREA.contains(myPosition())) {
                if(walking.walk(N_FISH_AREA)) {
                    task();
                }
            } else {
                warn("Could not find a fishing spot. This should never occur under normal conditions.");
                bot.getScriptExecutor().stop(false);
            }

        }
    }

    @Override
    boolean checkEnqueueTaskCondition() {
        return myPlayer().getAnimation() == IDLE_ID && !inventory.isFull();
    }
}
