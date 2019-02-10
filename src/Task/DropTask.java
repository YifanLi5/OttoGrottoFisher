package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.input.mouse.InventorySlotDestination;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DropTask extends PrioritizedReactiveTask {

    public DropTask(Bot bot) {
        super(bot);
        this.p = Priority.HIGH;
    }

    @Override
    void task() throws InterruptedException {
        long idleTime = randomNormalDist(8000, 3000);
        log("Before dropping fish, Idling for " + idleTime);
        sleep(idleTime);

        keyboard.pressKey(KeyEvent.VK_SHIFT);
        List<Integer> dropOrder = getRandomInvTraversalOrder(inventory.getItems(), random(0, 27));
        for(int invSlot: dropOrder) {
            Item itemAtInvSlot = inventory.getItemInSlot(invSlot);
            if(itemAtInvSlot.nameContains("Leaping trout", "Leaping salmon", "Leaping sturgeon")) {
                if(inventory.isItemSelected()) {
                    inventory.deselectItem();
                    keyboard.pressKey(KeyEvent.VK_SHIFT);
                }
                mouse.click(new InventorySlotDestination(bot, invSlot));
            }
        }
        keyboard.releaseKey(KeyEvent.VK_SHIFT);
    }

    @Override
    boolean checkEnqueueTaskCondition() {
        return inventory.isFull() && inventory.contains("Leaping trout", "Leaping salmon", "Leaping sturgeon");
    }

    //Use a breadth first traversal to produce a drop order
    private List<Integer> getRandomInvTraversalOrder(Item[] invItems, int startingInvIdx) {
        if(startingInvIdx < 0 || startingInvIdx > 27){
            throw new UnsupportedOperationException("input needs to in range [0-27].");
        }
        java.util.List<Integer> traversalOrder = new ArrayList<>(); //bfs traversal
        java.util.Queue<Integer> bfsQ = new LinkedList<>();
        boolean reverseOrder = false;
        boolean[] visitedSlots = new boolean[28];
        bfsQ.add(startingInvIdx);
        visitedSlots[startingInvIdx] = true;

        while(!bfsQ.isEmpty()) {
            int currentInvSlot = bfsQ.poll();
            traversalOrder.add(currentInvSlot);
            java.util.List<Integer> successors = getBFSSuccessors(currentInvSlot, reverseOrder);
            reverseOrder = !reverseOrder;  //used to reverse each successive level in bfs so that fish is dr
            successors.forEach(slot -> {
                if(!visitedSlots[slot]){
                    visitedSlots[slot] = true;
                    bfsQ.add(slot);
                }
            });

        }
        return traversalOrder;
    }

    private java.util.List<Integer> getBFSSuccessors(int invSlot, boolean reverseOrder) {
        List<Integer> successors = new ArrayList<>();
        boolean canUp = false, canRight = false, canDown = false, canLeft = false;
        if(!(invSlot <= 3)){ //up, cannot search up if invSlot is top 4 slots
            canUp = true;
        }
        if((invSlot + 1) % 4 != 0){ //right, cannot search right if invSlot is rightmost column
            canRight = true;
        }
        if(!(invSlot >= 24)){ //down, cannot search down if invSlot is bottom 4 slots
            canDown = true;
        }
        if(invSlot % 4 != 0){ //left, cannot search left if invSlot is leftmost column
            canLeft = true;
        }

        //can search in diagonal directions if can search in its composite directions
        //also, add in proper clockwise order.
        if(canUp) {
            successors.add(invSlot - 4);
        }
        if(canUp && canRight) {
            successors.add(invSlot - 3);
        }
        if(canRight) {
            successors.add(invSlot + 1);
        }
        if(canDown && canRight){
            successors.add(invSlot + 5);
        }
        if(canDown) {
            successors.add(invSlot + 4);
        }
        if(canDown && canLeft){
            successors.add(invSlot + 3);
        }
        if(canLeft) {
            successors.add(invSlot - 1);
        }
        if(canUp && canLeft){
            successors.add(invSlot - 5);
        }

        if(reverseOrder) {
            Collections.reverse(successors);
        }

        return successors;
    }

}
