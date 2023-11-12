package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.input.mouse.MouseDestination;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import static Task.ScriptConstants.*;

public class Idle extends Task {

    private final ConditionalSleep sleepWhileFishing = new ConditionalSleep(60000) {
        @Override
        public boolean condition() {
            return !(myPlayer().getAnimation() == FISHING_ANIM_ID);
        }
    };

    public Idle(Bot bot) {
        super(bot);
    }

    @Override
    boolean shouldRun() {
        return myPlayer().getAnimation() == FISHING_ANIM_ID;
    }

    @Override
    public void runTask() throws InterruptedException {
        shiftBottlesUp();
        log("Idling...");
        sleepWhileFishing.sleep();
        if (myPlayer().getAnimation() == -1) {
            long idleTime = randomGaussian(SESSION_MEAN, SESSION_STD_DEV);
            log(String.format("Simulating AFK for %dms", idleTime));
            sleep(idleTime);
        }
    }

    // Swap clue bottles to first slot occupied by a fish
    private void shiftBottlesUp() throws InterruptedException {
        int fishIdx = inventory.getSlot(ScriptConstants.BARBARIAN_FISH);
        int bottleIdx = inventory.getSlotForNameThatContains("Clue bottle");
        if (fishIdx != -1 && bottleIdx != -1 && bottleIdx > fishIdx) {
            log("DO NOT RESIZE SCREEN DURING INVENTORY SHIFT OPERATION!!!");
            sleep(1000);
            MouseDestination a = inventory.getMouseDestination(fishIdx);
            MouseDestination b = inventory.getMouseDestination(bottleIdx);
            mouse.continualClick(a, new Condition() {
                @Override
                public boolean evaluate() {
                    return mouse.move(b, true);
                }
            });
            log("Ok to resize.");
        }
    }
}

