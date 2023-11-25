package Task;

import Paint.ScriptPaint;
import org.osbot.rs07.Bot;
import org.osbot.rs07.input.mouse.MouseDestination;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import static Task.ScriptConstants.*;

public class Idle extends Task {

    private int conditionCheckCount = 0;
    private int conditionCheckThreshold = random(0,8);
    private final ConditionalSleep sleepUntilIdle = new ConditionalSleep(60000, 1000, 500) {

        @Override
        public boolean condition() {
            // hacky way of moving mouse offscreen after a random amount of time but only if the player is not idle
            // remember, condition returning true exits ConditionalSleep
            conditionCheckCount += 1;
            if(conditionCheckCount > conditionCheckThreshold && mouse.isOnScreen()) {
                mouse.moveOutsideScreen();
            }
            return myPlayer().getAnimation() == IDLE_ANIM_ID;
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
        conditionCheckCount = 0;
        conditionCheckThreshold = random(3,8);

        shiftBottlesUp();
        ScriptPaint.setStatus("Fishing... (Idle)");
        sleepUntilIdle.sleep();
        if (myPlayer().getAnimation() == -1 && !mouse.isOnScreen()) {
            ScriptPaint.setStatus("Simulating AFK");
            long idleTime = randomGaussian(SESSION_MEAN, SESSION_STD_DEV);
            log(String.format("Simulating AFK for %dms", idleTime));
            sleep(idleTime);
        }
    }

    // Swap clue bottles to first slot occupied by a fish
    private void shiftBottlesUp() throws InterruptedException {
        ScriptPaint.setStatus("Moving clue bottles. !!!DO NOT RESIZE CLIENT!!!");
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

