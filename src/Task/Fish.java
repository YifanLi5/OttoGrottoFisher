package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.filter.ActionFilter;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;

public class Fish extends Task {

    ActionFilter<NPC> fishingSpotFilter = new ActionFilter<>("Use-rod");
    public Fish(Bot bot) {
        super(bot);
    }

    @Override
    int probabilityWeight() {
        return 2;
    }

    @Override
    boolean shouldRun() {
        return myPlayer().getAnimation() == Task.IDLE_ID && !inventory.isFull();
    }

    @Override
    public void run() throws InterruptedException {
        if(!(inventory.contains("Barbarian rod") || inventory.contains("Pearl barbarian rod"))) {
            warn("Inventory does not contain a suitable Barbarian rod");
            bot.getScriptExecutor().stop(false);
        }

        if(!(inventory.contains("Feather") || inventory.contains("Bait") || inventory.contains("Fish offcuts"))) {
            warn("Inventory does not contain a suitable bait");
            bot.getScriptExecutor().stop(false);
        }

        if(inventory.isItemSelected()) {
            inventory.deselectItem();
        }

        final NPC[] fishingSpot = new NPC[1];
        boolean fishingSpotExists = new ConditionalSleep(5000) {
            @Override
            public boolean condition() {
                List<NPC> fishingSpots = npcs.filter(fishingSpotFilter);

                if(fishingSpots.isEmpty()) {
                    return false;
                }
                fishingSpot[0] = fishingSpots.get(random(fishingSpots.size()));
                return fishingSpot[0] != null;
            }
        }.sleep();

        if(!fishingSpotExists) {
            warn("Could not find a fishing spot.");
            bot.getScriptExecutor().stop(false);
        }
        if(!fish(fishingSpot[0])) {
            warn("Script Unable to fish");
            bot.getScriptExecutor().stop(false);
        }
    }

    private boolean fish(NPC fishSpot) {
        InteractionEvent fishEvent = new InteractionEvent(fishSpot, "Use-rod");
        fishEvent.setOperateCamera(false);
        execute(fishEvent);
        new ConditionalSleep(5000) {
            @Override
            public boolean condition() {
                return myPlayer().isAnimating() || myPlayer().isMoving();
            }
        }.sleep();
        return fishEvent.hasFinished();
    }
}
