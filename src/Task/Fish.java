package Task;

import Paint.ScriptPaint;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.filter.ActionFilter;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.utility.ConditionalLoop;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;

import static Task.ScriptConstants.BAITS;
import static Task.ScriptConstants.FISHING_ANIM_ID;

public class Fish extends Task {

    class FindSpotThenFishLoop extends ConditionalLoop {

        private final ActionFilter<NPC> fishingSpotFilter = new ActionFilter<>(ScriptConstants.USE_ROD);
        private final Filter<NPC> closeAndVisibleFilter = npc -> npc.isVisible() && npc.getPosition().distance(myPlayer().getPosition()) <= 5;

        public FindSpotThenFishLoop(Bot bot, int i) {
            super(bot, i);
        }

        @Override
        public boolean condition() {
            NPC fishingSpot;
            List<NPC> fishingSpots = npcs.filter(fishingSpotFilter, closeAndVisibleFilter);
            if (fishingSpots.isEmpty()) {
                log("Fishing Spot filter found nothing. Resorting to npcs.closest fallback...");
                fishingSpot = npcs.closest(fishingSpotFilter);
            } else {
                fishingSpot = fishingSpots.get(random(fishingSpots.size()));
            }

            if(fishingSpot != null) {
                Event fishEvent = new InteractionEvent(fishingSpot, "Use-rod")
                        .setOperateCamera(false)
                        .setBlocking();

                execute(fishEvent);
                if(fishEvent.hasFailed()) {
                    warn("Fish event failed. Retrying...");
                }
            } else {
                warn("Did not find a fishing Spot. Retrying...");
            }

            boolean result = new ConditionalSleep(5000) {
                @Override
                public boolean condition() {
                    return myPlayer().getAnimation() == FISHING_ANIM_ID;
                }
            }.sleep();
            return !result;
        }
    }
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
    public void runTask() throws InterruptedException {
        if (!(inventory.contains("Barbarian rod", "Pearl barbarian rod"))) {
            warn("Inventory does not contain a suitable Barbarian rod");
            bot.getScriptExecutor().stop(false);
        }

        if (!(inventory.contains(BAITS))) {
            warn("Inventory does not contain suitable bait");
            bot.getScriptExecutor().stop(false);
        }

        if (inventory.isItemSelected()) {
            inventory.deselectItem();
        }

        if (equipment.isWieldingWeapon("Dragon harpoon") && combat.getSpecialPercentage() == 100) {
            ScriptPaint.setStatus("Dragon harpoon special");
            combat.toggleSpecialAttack(true);
        }
        ScriptPaint.setStatus("Querying Fishing spots");
        ConditionalLoop loop = new FindSpotThenFishLoop(bot, 5);
        loop.start();
        if(!loop.getResult()) {
            warn("Unable to find a fishing spot and fish. Exiting...");
            bot.getScriptExecutor().stop(false);
        }
        ScriptPaint.setStatus("Fishing...");
    }
}


