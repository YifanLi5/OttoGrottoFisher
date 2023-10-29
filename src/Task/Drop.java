package Task;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.InventorySlotDestination;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Drop extends Task {

    private final static int[][] DROP_ORDERS = {
            {1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26, 3, 7, 11, 15, 19, 23, 27, 4, 8, 12, 16, 20, 24, 28},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28},
            {28, 24, 20, 16, 12, 8, 4, 27, 23, 19, 15, 11, 7, 3, 26, 22, 18, 14, 10, 6, 2, 25, 21, 17, 13, 9, 5, 1},
            {25, 21, 17, 13, 9, 5, 1, 26, 22, 18, 14, 10, 6, 2, 27, 23, 19, 15, 11, 7, 3, 28, 24, 20, 16, 12, 8, 4},
            {3, 4, 2, 1, 8, 7, 6, 5, 9, 10, 11, 12, 16, 15, 14, 13, 17, 18, 19, 20, 24, 23, 22, 21, 25, 26, 27, 28},
            {1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12, 13, 17, 14, 18, 15, 19, 16, 20, 21, 25, 22, 26, 23, 27, 24, 28}
    };
    private final static HashMap<Integer, InventorySlotDestination> INVENTORY_SLOT_DESTINATION_MAPPING = new HashMap<>();
    private final static String[] BARBARIAN_FISH = {"Leaping trout", "Leaping salmon", "Leaping sturgeon"};

    public Drop(Bot bot) {
        super(bot);
        for(int i = 1; i <= 28; i++) {
            INVENTORY_SLOT_DESTINATION_MAPPING.put(i, new InventorySlotDestination(bot, i));
        }
    }

    @Override
    int probabilityWeight() {
        return 4;
    }

    @Override
    boolean shouldRun() {
        boolean doPrematureDrop = myPlayer().getAnimation() == IDLE_ID && inventory.getEmptySlots() < random(4, 12);
        return (inventory.isFull() || doPrematureDrop) && inventory.contains(BARBARIAN_FISH);
    }

    @Override
    public void run() throws InterruptedException {
        long idleTime = randomNormalDist(8000, 3000);

        log("Before dropping fish, Idling for " + idleTime);
        sleep(idleTime);
        tabs.open(Tab.INVENTORY);
        customDropAll();

    }

    boolean customDropAll() {
        int[] dropOrder = DROP_ORDERS[random(0,DROP_ORDERS.length-1)];
        keyboard.pressKey(KeyEvent.VK_SHIFT);
        for(int invSlot: dropOrder) {

            if(inventory.isItemSelected()) {
                inventory.deselectItem();
                keyboard.pressKey(KeyEvent.VK_SHIFT);
            }
            Item itemAtInvSlot = inventory.getItemInSlot(invSlot);
            if(itemAtInvSlot == null || !itemAtInvSlot.nameContains(BARBARIAN_FISH) || random(100) <= 3) {
                continue;
            }
            mouse.click(INVENTORY_SLOT_DESTINATION_MAPPING.get(invSlot));
        }

        if(inventory.contains(BARBARIAN_FISH)) {
            inventory.dropAll();
        }

        return !inventory.contains(BARBARIAN_FISH);
    }
}
