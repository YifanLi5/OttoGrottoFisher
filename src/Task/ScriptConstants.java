package Task;

import java.util.concurrent.ThreadLocalRandom;

public class ScriptConstants {
    public static final int FISHING_ANIM_ID = 9350;
    public final static String[] BARBARIAN_FISH = {"Leaping trout", "Leaping salmon", "Leaping sturgeon"};

    public final static String[] DO_NOT_DROP = {"Feather", "Bait", "Fish offcuts", "Barbarian rod", "Pearl barbarian rod", "Clue bottle"};

    public final static String[] BAITS = {"Feather", "Bait", "Fish offcuts"};

    public final static String USE_ROD = "Use-rod";

    public static final int sessionMean;

    public static final int sessionStdDev;

    static {
        sessionMean = ThreadLocalRandom.current().nextInt(7500, 15000);
        sessionStdDev = ThreadLocalRandom.current().nextInt(3000, 6000);
    }

}
