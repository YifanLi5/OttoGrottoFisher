package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.InventorySlotDestination;

import java.awt.event.KeyEvent;

public class DropTask extends PrioritizedReactiveTask {


    //arrays are inventory slot traversal order created originally through running a bf traversal algorithm on every starting inventory slot.
    private final static int[][] bfsDropOrders = {
            {0, 1, 5, 4, 8, 9, 10, 6, 2, 3, 7, 11, 15, 14, 13, 12, 16, 17, 18, 19, 23, 22, 21, 20, 24, 25, 26, 27},
            {1, 2, 6, 5, 4, 0, 8, 9, 10, 11, 7, 3, 15, 14, 13, 12, 16, 17, 18, 19, 23, 22, 21, 20, 24, 25, 26, 27},
            {2, 3, 7, 6, 5, 1, 0, 4, 8, 9, 10, 11, 15, 14, 13, 12, 16, 17, 18, 19, 23, 22, 21, 20, 24, 25, 26, 27},
            {3, 7, 6, 2, 1, 5, 9, 10, 11, 15, 14, 13, 12, 8, 4, 0, 16, 17, 18, 19, 23, 22, 21, 20, 24, 25, 26, 27},
            {4, 0, 1, 5, 9, 8, 12, 13, 14, 10, 6, 2, 3, 7, 11, 15, 19, 18, 17, 16, 20, 21, 22, 23, 27, 26, 25, 24},
            {5, 1, 2, 6, 10, 9, 8, 4, 0, 12, 13, 14, 15, 11, 7, 3, 19, 18, 17, 16, 20, 21, 22, 23, 27, 26, 25, 24},
            {6, 2, 3, 7, 11, 10, 9, 5, 1, 0, 4, 8, 12, 13, 14, 15, 19, 18, 17, 16, 20, 21, 22, 23, 27, 26, 25, 24},
            {7, 3, 11, 10, 6, 2, 1, 5, 9, 13, 14, 15, 19, 18, 17, 16, 12, 8, 4, 0, 20, 21, 22, 23, 27, 26, 25, 24},
            {8, 4, 5, 9, 13, 12, 16, 17, 18, 14, 10, 6, 2, 1, 0, 3, 7, 11, 15, 19, 23, 22, 21, 20, 24, 25, 26, 27},
            {9, 5, 6, 10, 14, 13, 12, 8, 4, 16, 17, 18, 19, 15, 11, 7, 3, 0, 2, 1, 23, 22, 21, 20, 24, 25, 26, 27},
            {10, 6, 7, 11, 15, 14, 13, 9, 5, 0, 4, 8, 12, 16, 17, 18, 19, 1, 3, 2, 23, 22, 21, 20, 24, 25, 26, 27},
            {11, 7, 15, 14, 10, 6, 1, 5, 9, 13, 17, 18, 19, 2, 3, 23, 22, 21, 20, 16, 12, 8, 4, 0, 24, 25, 26, 27},
            {12, 8, 9, 13, 17, 16, 20, 21, 22, 18, 14, 10, 6, 5, 4, 0, 2, 3, 1, 7, 11, 15, 19, 23, 27, 26, 25, 24},
            {13, 9, 10, 14, 18, 17, 16, 12, 8, 20, 21, 22, 23, 19, 15, 11, 7, 4, 6, 5, 0, 1, 3, 2, 27, 26, 25, 24},
            {14, 10, 11, 15, 19, 18, 17, 13, 9, 4, 8, 12, 16, 20, 21, 22, 23, 5, 7, 6, 3, 2, 27, 26, 25, 24, 0, 1},
            {15, 11, 19, 18, 14, 10, 5, 9, 13, 17, 21, 22, 23, 6, 7, 3, 27, 26, 25, 24, 20, 16, 12, 1, 2, 8, 4, 0},
            {16, 12, 13, 17, 21, 20, 24, 25, 26, 22, 18, 14, 10, 9, 8, 4, 6, 7, 5, 11, 15, 19, 23, 27, 3, 2, 1, 0},
            {17, 13, 14, 18, 22, 21, 20, 16, 12, 24, 25, 26, 27, 23, 19, 15, 11, 8, 10, 9, 4, 5, 7, 6, 3, 2, 1, 0},
            {18, 14, 15, 19, 23, 22, 21, 17, 13, 8, 12, 16, 20, 24, 25, 26, 27, 9, 11, 10, 7, 6, 4, 5, 0, 1, 2, 3},
            {19, 15, 23, 22, 18, 14, 9, 13, 17, 21, 25, 26, 27, 10, 11, 7, 24, 20, 16, 5, 6, 12, 8, 4, 0, 1, 2, 3},
            {20, 16, 17, 21, 25, 24, 26, 22, 18, 14, 13, 12, 8, 10, 11, 9, 15, 19, 23, 27, 7, 6, 5, 4, 0, 1, 3, 2},
            {21, 17, 18, 22, 26, 25, 24, 20, 16, 27, 23, 19, 15, 12, 14, 13, 8, 9, 11, 10, 7, 6, 5, 4, 0, 1, 3, 2},
            {22, 18, 19, 23, 27, 26, 25, 21, 17, 12, 16, 20, 24, 13, 15, 14, 11, 10, 8, 9, 4, 5, 6, 7, 3, 2, 0, 1},
            {23, 19, 27, 26, 22, 18, 13, 17, 21, 25, 14, 15, 11, 24, 20, 9, 10, 16, 12, 8, 4, 5, 6, 7, 3, 2, 0, 1},
            {24, 20, 21, 25, 26, 22, 18, 17, 16, 12, 14, 15, 13, 19, 23, 27, 11, 10, 9, 8, 4, 5, 7, 6, 3, 2, 1, 0},
            {25, 21, 22, 26, 24, 20, 27, 23, 19, 16, 18, 17, 12, 13, 15, 14, 11, 10, 9, 8, 4, 5, 7, 6, 3, 2, 1, 0},
            {26, 22, 23, 27, 25, 21, 16, 20, 24, 17, 19, 18, 15, 14, 12, 13, 8, 9, 10, 11, 7, 6, 4, 5, 0, 1, 2, 3},
            {27, 23, 26, 22, 17, 21, 25, 18, 19, 15, 24, 13, 14, 20, 16, 12, 8, 9, 10, 11, 7, 6, 4, 5, 0, 1, 2, 3}
    };

    private static final int IDLE_ID = -1;

    public DropTask(Bot bot) {
        super(bot);
        this.p = Priority.HIGH;
    }

    @Override
    void task() throws InterruptedException {
        long idleTime = randomNormalDist(8000, 3000);
        tabs.open(Tab.INVENTORY);
        log("Before dropping fish, Idling for " + idleTime);
        sleep(idleTime);

        keyboard.pressKey(KeyEvent.VK_SHIFT);
        int[] dropOrder = bfsDropOrders[random(0,bfsDropOrders.length-1)];
        for(int invSlot: dropOrder) {
            Item itemAtInvSlot = inventory.getItemInSlot(invSlot);
            if(itemAtInvSlot != null && itemAtInvSlot.nameContains("Leaping trout", "Leaping salmon", "Leaping sturgeon")) {
                if(inventory.isItemSelected()) {
                    log("item is selected, deselecting...");
                    inventory.deselectItem();
                    keyboard.pressKey(KeyEvent.VK_SHIFT);
                    sleep(1000);
                }
                mouse.click(new InventorySlotDestination(bot, invSlot));
            }
        }
        keyboard.releaseKey(KeyEvent.VK_SHIFT);
    }

    //drop all fish if the inventory has filled up OR almost filled up. ex: It may be preferable to not re-fish if there are only  5 slots left.
    @Override
    boolean shouldTaskActivate() {
        boolean doPrematureDrop = inventory.getEmptySlotCount() < 8 && myPlayer().getAnimation() == IDLE_ID;
        return (inventory.isFull() || doPrematureDrop) && inventory.contains("Leaping trout", "Leaping salmon", "Leaping sturgeon");
    }

}
