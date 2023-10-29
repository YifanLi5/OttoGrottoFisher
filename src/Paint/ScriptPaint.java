package Paint;

import org.osbot.rs07.api.Skills;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.canvas.paint.Painter;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.script.Script;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScriptPaint extends BotMouseListener implements Painter {
    private static final Color GRAY = new Color(70, 61, 50, 156);
    private static final String IMG_FOLDER = "resources", STR = "/str.png", AGILITY = "/agility.png", FISHING = "/fishing.png";
    private final static Rectangle TOGGLE_PAINT_VISIBILITY = new Rectangle(0, 291, 47, 47);
    private boolean paintVisible = false;
    private BufferedImage str, agility, fishing;
    private final Script script;
    private final long startTime;
    private final ExperienceTracker tracker;
    public ScriptPaint(Script script){
        this.script = script;
        try {
            str = ImageIO.read(script.getScriptResourceAsStream(IMG_FOLDER + STR));
            agility = ImageIO.read(script.getScriptResourceAsStream(IMG_FOLDER + AGILITY));
            fishing = ImageIO.read(script.getScriptResourceAsStream(IMG_FOLDER + FISHING));
        } catch (IOException e) {
            script.log(e);
        }
        script.getBot().addPainter(this);
        script.getBot().addMouseListener(this);
        startTime = System.currentTimeMillis();
        tracker = script.getExperienceTracker();
        tracker.start(Skill.FISHING);
        tracker.start(Skill.STRENGTH);
        tracker.start(Skill.AGILITY);
    }

    @Override
    public void onPaint(Graphics2D g) {
        drawShowStatsBtn(g);
        drawMouse(g);
        drawRuntime(g);
        if(paintVisible) {
            String fishingXpGained = formatValue(tracker.getGainedXP(Skill.FISHING));
            String strXpGained = formatValue(tracker.getGainedXP(Skill.STRENGTH));
            String agilityXpGained = formatValue(tracker.getGainedXP(Skill.AGILITY));
            String fishingXPH = formatValue(tracker.getGainedXPPerHour(Skill.FISHING));
            String strXPH = formatValue(tracker.getGainedXPPerHour(Skill.STRENGTH));
            String agilityXPH = formatValue(tracker.getGainedXPPerHour(Skill.AGILITY));
            String fishingTTL = formatTime(tracker.getTimeToLevel(Skill.FISHING));
            String strTTL = formatTime(tracker.getTimeToLevel(Skill.STRENGTH));
            String agilityTTL = formatTime(tracker.getTimeToLevel(Skill.AGILITY));
            drawImgs(g);
            drawTopLabel(g);
            drawImgs(g);
            drawLVLs(g);
            drawLvlsGained(g);
            drawXpGained(g, fishingXpGained, strXpGained, agilityXpGained);
            drawXPH(g, fishingXPH, strXPH, agilityXPH);
            drawTTL(g, fishingTTL, strTTL, agilityTTL);
        }
    }

    @Override
    public void checkMouseEvent(MouseEvent mouseEvent) {
        switch (mouseEvent.getID()){
            case MouseEvent.MOUSE_PRESSED:
                Point clickPt = mouseEvent.getPoint();
                if(TOGGLE_PAINT_VISIBILITY.contains(clickPt)){
                    paintVisible = !paintVisible;
                    mouseEvent.consume();
                }
        }
    }

    private void drawMouse(Graphics2D g){
        Point mP = script.getMouse().getPosition();
        g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);
    }

    private void drawShowStatsBtn(Graphics2D g){
        g.setColor(GRAY);
        g.fill(TOGGLE_PAINT_VISIBILITY);
        g.setColor(Color.WHITE);
        g.drawString(paintVisible ? "hide" : "show", TOGGLE_PAINT_VISIBILITY.x + 8, TOGGLE_PAINT_VISIBILITY.y + 32);
    }

    private void drawTopLabel(Graphics2D g){
        final int X_ORIGIN = 47, Y_ORIGIN = 291, FULL_WIDTH = 472, COLUMN_WIDTH = 94, HEIGHT = 47, TXT_Y_OFFSET = 32, TXT_X_OFFSET = 32;
        g.setColor(GRAY);
        g.fillRect(X_ORIGIN, Y_ORIGIN, FULL_WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("LVL", X_ORIGIN + TXT_X_OFFSET, Y_ORIGIN + TXT_Y_OFFSET);
        g.drawString("+LVL", X_ORIGIN + COLUMN_WIDTH + TXT_X_OFFSET, Y_ORIGIN + TXT_Y_OFFSET);
        g.drawString("+XP", X_ORIGIN + 2*COLUMN_WIDTH + TXT_X_OFFSET, Y_ORIGIN + TXT_Y_OFFSET);
        g.drawString("XP/H", X_ORIGIN + 3*COLUMN_WIDTH + TXT_X_OFFSET, Y_ORIGIN + TXT_Y_OFFSET);
        g.drawString("TTL", X_ORIGIN + 4*COLUMN_WIDTH + TXT_X_OFFSET, Y_ORIGIN + TXT_Y_OFFSET);

        g.drawLine(X_ORIGIN, Y_ORIGIN, X_ORIGIN, Y_ORIGIN + 185);
        g.drawLine(X_ORIGIN + COLUMN_WIDTH, Y_ORIGIN, X_ORIGIN + COLUMN_WIDTH, Y_ORIGIN + 185);
        g.drawLine(X_ORIGIN + 2*COLUMN_WIDTH, Y_ORIGIN, X_ORIGIN + 2*COLUMN_WIDTH, Y_ORIGIN + 185);
        g.drawLine(X_ORIGIN + 3*COLUMN_WIDTH, Y_ORIGIN, X_ORIGIN + 3*COLUMN_WIDTH, Y_ORIGIN + 185);
        g.drawLine(X_ORIGIN + 4*COLUMN_WIDTH, Y_ORIGIN, X_ORIGIN + 4*COLUMN_WIDTH, Y_ORIGIN + 185);
        g.drawLine(X_ORIGIN + 5*COLUMN_WIDTH, Y_ORIGIN, X_ORIGIN + 5*COLUMN_WIDTH, Y_ORIGIN + 185);
    }


    //column 0
    private void drawImgs(Graphics2D g){
        final int X_ORIGIN = 0, Y_ORIGIN = 338, WIDTH = 47, HEIGHT = 47, IMG_OFFSET_X = 8, IMG_OFFSET_Y = 8;
        g.setColor(GRAY);
        g.fillRect(X_ORIGIN, Y_ORIGIN, WIDTH, HEIGHT);
        g.drawImage(fishing, null, X_ORIGIN + IMG_OFFSET_X, Y_ORIGIN + IMG_OFFSET_Y);
        g.fillRect(X_ORIGIN, Y_ORIGIN + HEIGHT, WIDTH, HEIGHT);
        g.drawImage(str, null, X_ORIGIN + IMG_OFFSET_X, Y_ORIGIN + HEIGHT + IMG_OFFSET_Y);
        g.fillRect(X_ORIGIN, Y_ORIGIN + 2*HEIGHT, WIDTH, HEIGHT);
        g.drawImage(agility, null, X_ORIGIN + IMG_OFFSET_X, Y_ORIGIN + 2*HEIGHT + IMG_OFFSET_Y);

        g.setColor(Color.WHITE);
        g.drawLine(X_ORIGIN, Y_ORIGIN, X_ORIGIN + 520, Y_ORIGIN);
        g.drawLine(X_ORIGIN, Y_ORIGIN + HEIGHT, X_ORIGIN + 520, Y_ORIGIN + HEIGHT);
        g.drawLine(X_ORIGIN, Y_ORIGIN + 2*HEIGHT, X_ORIGIN + 520, Y_ORIGIN + 2*HEIGHT);
    }

    //column 1
    private void drawLVLs(Graphics2D g){
        int X_ORIGIN = 47, Y_ORIGIN = 338, COLUMN_WIDTH = 94, FULL_COLUMN_HEIGHT = 142, COLUMN_HEIGHT = 47, TXT_OFFSET = 32;
        g.setColor(GRAY);
        g.fillRect(X_ORIGIN, Y_ORIGIN, COLUMN_WIDTH, FULL_COLUMN_HEIGHT);
        g.setColor(Color.WHITE);

        Skills skills = script.getSkills();
        g.drawString(String.valueOf(skills.getStatic(Skill.FISHING)), X_ORIGIN + TXT_OFFSET, Y_ORIGIN + TXT_OFFSET);
        g.drawString(String.valueOf(skills.getStatic(Skill.STRENGTH)), X_ORIGIN + TXT_OFFSET, Y_ORIGIN + COLUMN_HEIGHT + TXT_OFFSET);
        g.drawString(String.valueOf(skills.getStatic(Skill.AGILITY)), X_ORIGIN + TXT_OFFSET, Y_ORIGIN + 2*COLUMN_HEIGHT + TXT_OFFSET);
    }


    //column 2
    private void drawLvlsGained(Graphics2D g){
        final int X_ORIGIN = 141, Y_ORIGIN = 338, COLUMN_WIDTH = 94, FULL_COLUMN_HEIGHT = 142, COLUMN_HEIGHT = 47, TXT_OFFSET = 32;
        g.setColor(GRAY);
        g.fillRect(X_ORIGIN, Y_ORIGIN, COLUMN_WIDTH, FULL_COLUMN_HEIGHT);
        g.setColor(Color.WHITE);

        Skills skills = script.getSkills();
        g.drawString(String.valueOf(script.getExperienceTracker().getGainedLevels(Skill.FISHING)), X_ORIGIN + TXT_OFFSET, Y_ORIGIN + TXT_OFFSET);
        g.drawString(String.valueOf(script.getExperienceTracker().getGainedLevels(Skill.STRENGTH)), X_ORIGIN + TXT_OFFSET, Y_ORIGIN + COLUMN_HEIGHT + TXT_OFFSET);
        g.drawString(String.valueOf(script.getExperienceTracker().getGainedLevels(Skill.AGILITY)), X_ORIGIN + TXT_OFFSET, Y_ORIGIN + 2*COLUMN_HEIGHT + TXT_OFFSET);
    }

    //column 3
    private void drawXpGained(Graphics2D g, String fishingXpGained, String strXpGained, String agilityXpGained){
        final int X_ORIGIN = 235, Y_ORIGIN = 338, COLUMN_WIDTH = 94, FULL_COLUMN_HEIGHT = 142, COLUMN_HEIGHT = 47, TXT_OFFSET = 32;
        g.setColor(GRAY);
        g.fillRect(X_ORIGIN, Y_ORIGIN, COLUMN_WIDTH, FULL_COLUMN_HEIGHT);
        g.setColor(Color.WHITE);

        g.drawString(fishingXpGained, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + TXT_OFFSET);
        g.drawString(strXpGained, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + COLUMN_HEIGHT + TXT_OFFSET);
        g.drawString(agilityXpGained, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + 2*COLUMN_HEIGHT + TXT_OFFSET);
    }

    //column 4
    private void drawXPH(Graphics2D g, String fishingXPH, String strXPH, String agilityXPH){
        final int X_ORIGIN = 329, Y_ORIGIN = 338, COLUMN_WIDTH = 94, FULL_COLUMN_HEIGHT = 142, COLUMN_HEIGHT = 47, TXT_OFFSET = 32;
        g.setColor(GRAY);
        g.fillRect(X_ORIGIN, Y_ORIGIN, COLUMN_WIDTH, FULL_COLUMN_HEIGHT);
        g.setColor(Color.WHITE);

        g.drawString(fishingXPH, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + TXT_OFFSET);
        g.drawString(strXPH, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + COLUMN_HEIGHT + TXT_OFFSET);
        g.drawString(agilityXPH, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + 2*COLUMN_HEIGHT + TXT_OFFSET);
    }

    //column 5
    private void drawTTL(Graphics2D g, String fishingTTL, String strTTL, String agilityTTL){
        final int X_ORIGIN = 423, Y_ORIGIN = 338, COLUMN_WIDTH = 94, FULL_COLUMN_HEIGHT = 142, COLUMN_HEIGHT = 47, TXT_OFFSET = 32;
        g.setColor(GRAY);
        g.fillRect(X_ORIGIN, Y_ORIGIN, COLUMN_WIDTH, FULL_COLUMN_HEIGHT);
        g.setColor(Color.WHITE);

        g.drawString(fishingTTL, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + TXT_OFFSET);
        g.drawString(strTTL, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + COLUMN_HEIGHT + TXT_OFFSET);
        g.drawString(agilityTTL, X_ORIGIN + TXT_OFFSET, Y_ORIGIN + 2*COLUMN_HEIGHT + TXT_OFFSET);
    }

    private void drawRuntime(Graphics2D g){
        final int X_ORIGIN = 404, Y_ORIGIN = 480, WIDTH = 111, HEIGHT = 22, TXT_X_OFFSET = 31, TXT_Y_OFFSET = 15;
        g.setColor(Color.RED);
        g.fillRect(X_ORIGIN, Y_ORIGIN, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        String runtime = String.valueOf(formatTime(System.currentTimeMillis() - startTime));
        g.drawString(runtime, X_ORIGIN + TXT_X_OFFSET, Y_ORIGIN + TXT_Y_OFFSET);
    }

    private String formatTime(final long ms){
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60; m %= 60; h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private String formatValue(final long l) {
        return (l > 1_000_000) ? String.format("%.2fm", ((double) l / 1_000_000))
                : (l > 1000) ? String.format("%.1fk", ((double) l / 1000))
                : l + "";
    }
}
