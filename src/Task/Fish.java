package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.filter.ActionFilter;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;

import static Task.ScriptConstants.FISHING_ANIM_ID;

public class Fish extends Task {

    private final ActionFilter<NPC> fishingSpotFilter = new ActionFilter<>(ScriptConstants.USE_ROD);
    private final Filter<NPC> closeAndVisibleFilter = npc -> npc.isVisible() && npc.getPosition().distance(myPlayer().getPosition()) <= 5;

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
        if (!(inventory.contains("Barbarian rod", "Pearl barbarian rod"))) {
            warn("Inventory does not contain a suitable Barbarian rod");
            bot.getScriptExecutor().stop(false);
        }

        if (!(inventory.contains("Feather", "Bait", "Fish offcuts"))) {
            warn("Inventory does not contain suitable bait");
            bot.getScriptExecutor().stop(false);
        }

        if (inventory.isItemSelected()) {
            inventory.deselectItem();
        }

        if (equipment.isWieldingWeapon("Dragon harpoon") && combat.getSpecialPercentage() == 100) {
            combat.toggleSpecialAttack(true);
        }

        final NPC[] fishingSpot = new NPC[1];
        boolean fishingSpotExists = new ConditionalSleep(2500) {
            @Override
            public boolean condition() {
                List<NPC> fishingSpots = npcs.filter(fishingSpotFilter, closeAndVisibleFilter);
                log(String.format("Found %d fishing_spots", fishingSpots.size()));
                if (fishingSpots.isEmpty()) {
                    log("Fishing Spot filter found nothing. Resorting to npcs.closest fallback...");
                    fishingSpot[0] = npcs.closest(fishingSpotFilter);
                } else {
                    fishingSpot[0] = fishingSpots.get(random(fishingSpots.size()));
                }

                return fishingSpot[0] != null;
            }
        }.sleep();

        boolean doloop = false;
        int stuckCounter = 0;
        do {
            if (!fishingSpotExists) {
                stuckCounter += 1;
                warn(String.format("Could not find a fishing spot. %d/5", stuckCounter));
                continue;
            }
            doloop = !fish(fishingSpot[0]);
            if (doloop) {
                warn(String.format("Script Unable to fish. Retrying... %d/5", stuckCounter));
                stuckCounter += 1;
            }
        } while (doloop && stuckCounter < 5);
        if (stuckCounter >= 5) {
            bot.getScriptExecutor().stop(false);
        }

        mouse.moveOutsideScreen();
    }

    private boolean fish(NPC fishSpot) {
        if (fishSpot == null) {
            warn("fishSpot is null");
            return false;
        }
        InteractionEvent fishEvent = new InteractionEvent(fishSpot, "Use-rod");
        fishEvent.setOperateCamera(false);
        execute(fishEvent);
        new ConditionalSleep(10000) {
            @Override
            public boolean condition() {
                return fishEvent.hasFinished() && myPlayer().getAnimation() == FISHING_ANIM_ID;
            }
        }.sleep();
        return myPlayer().getAnimation() == FISHING_ANIM_ID;
    }
}