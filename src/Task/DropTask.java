package Task;

import org.osbot.rs07.Bot;

public class DropTask extends PrioritizedReactiveTask {


    //iterate forward or reverse to get the other bottom->top or right->left drop orders
    //row by row
    private static final int[] INV_SLOT_DROP_ORDER_0 = {
        0, 1, 2, 3,
        4, 5, 6, 7,
        8, 9, 10, 11,
        12, 13, 14, 15,
        16, 17, 18, 19,
        20, 21, 22, 23,
        24, 25, 26, 27
    };

    //column by column
    private static final int[] INV_SLOT_DROP_ORDER_1 = {
        0, 4, 8, 12, 16, 20, 24,
        1, 5, 9, 13, 17, 21, 25,
        2, 6, 10, 14, 18, 22, 26,
        3, 7, 11, 15, 19, 23, 27
    };


    public DropTask(Bot bot) {
        super(bot);
        this.p = Priority.HIGH;
    }

    @Override
    void task() throws InterruptedException {
        //drop all fish from inventory using a drop pattern
    }

    @Override
    boolean checkEnqueueTaskCondition() {
        return inventory.isFull() && inventory.contains("Leaping trout", "Leaping salmon", "Leaping sturgeon");
    }
}
