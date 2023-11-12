package Task;

import java.util.concurrent.ThreadLocalRandom;

public class ScriptConstants {
    public static final int FISHING_ANIM_ID = 9350;
    public final static String[] BARBARIAN_FISH = {"Leaping trout", "Leaping salmon", "Leaping sturgeon"};

    public final static String[] DO_NOT_DROP = {"Feather", "Bait", "Fish offcuts", "Barbarian rod", "Pearl barbarian rod", "Clue bottle"};

    public final static String[] BAITS = {"Feather", "Bait", "Fish offcuts"};

    public final static String USE_ROD = "Use-rod";

    public static final int SESSION_MEAN;

    public static final int SESSION_STD_DEV;

    public static final int SESSION_DROP_SKIP;

    static {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        SESSION_MEAN = current.nextInt(7500, 10000);
        SESSION_STD_DEV = current.nextInt(1500, 3000);
        SESSION_DROP_SKIP = current.nextInt(30);
    }

}
