package Paint;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.canvas.paint.Painter;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.script.Script;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ScriptPaint extends BotMouseListener implements Painter {
    private static final Color GRAY = new Color(70, 61, 50, 156);
    private boolean showPaint = true;
    private final Script script;
    private final long startTime;
    private final ExperienceTracker tracker;

    private final int startLvlFishing;
    private final int startLvlStr;
    private final int startLvlAgility;

    private final String[][] data = {
        {"", "+XP (XP/H)", "LVL (+)"},
        {"Fishing", "", ""},
        {"Strength", "", ""},
        {"Agility", "", ""}
    };

    private final int cellWidth = 100;
    private final int cellHeight = 50;

    private final Rectangle runtimeRect = new Rectangle(0, cellHeight * data.length, cellWidth * data[0].length, 25);

    public ScriptPaint(Script script) {
        this.script = script;
        script.getBot().addPainter(this);
        script.getBot().addMouseListener(this);

        startLvlFishing = script.skills.getStatic(Skill.FISHING);
        startLvlStr = script.skills.getStatic(Skill.STRENGTH);
        startLvlAgility = script.skills.getStatic(Skill.AGILITY);

        startTime = System.currentTimeMillis();
        tracker = script.getExperienceTracker();
        tracker.start(Skill.FISHING);
        tracker.start(Skill.STRENGTH);
        tracker.start(Skill.AGILITY);
    }

    @Override
    public void onPaint(Graphics2D g2d) {
        drawMouse(g2d);
        populateDataGrid();
        drawXpGrid(g2d, data, 0, 0, cellWidth, cellHeight, showPaint);
        drawRuntime(g2d, runtimeRect, showPaint);
    }

    private void populateDataGrid() {
        data[1][1] = String.format("+%s (%s)", formatNumber(tracker.getGainedXP(Skill.FISHING)), formatNumber(tracker.getGainedXPPerHour(Skill.FISHING)));
        data[1][2] = String.format("%s (+%s)", startLvlFishing, tracker.getGainedLevels(Skill.FISHING));

        data[2][1] = String.format("+%s (%s)", formatNumber(tracker.getGainedXP(Skill.STRENGTH)), formatNumber(tracker.getGainedXPPerHour(Skill.STRENGTH)));
        data[2][2] = String.format("%s (+%s)", startLvlStr, tracker.getGainedLevels(Skill.STRENGTH));

        data[3][1] = "^";
        data[3][2] = String.format("%s (+%s)", startLvlAgility, tracker.getGainedLevels(Skill.AGILITY));
    }

    private void drawXpGrid(
            Graphics2D g2d,
            String[][] data,
            int originX,
            int originY,
            int cellWidth,
            int cellHeight,
            boolean showPaint
    ) {

        if(showPaint) {
            data[0][0] = "--Hide--";
        } else {
            data = new String[][]{{"--Show--"}};
        }
        int numRows = data.length;
        int numCols = data[0].length;
        int gridWidth = numCols * cellWidth;
        int gridHeight = numRows * cellHeight;

        g2d.setColor(GRAY);
        g2d.fillRect(originX, originY, gridWidth, gridHeight);

        g2d.setColor(Color.WHITE);

        for (int i = 0; i <= numRows; i++) {
            int y = originY + i * cellHeight;
            g2d.drawLine(originX, y, originX + gridWidth, y);
        }

        for (int i = 0; i <= numCols; i++) {
            int x = originX + i * cellWidth;
            g2d.drawLine(x, originY, x, originY + gridHeight);
        }

        Font font = new Font("Arial", Font.PLAIN, 14);
        g2d.setFont(font);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                String cellData = data[i][j];
                int x = originX + j * cellWidth + (cellWidth - g2d.getFontMetrics().stringWidth(cellData)) / 2;
                int y = originY + i * cellHeight + (cellHeight - g2d.getFontMetrics().getHeight()) / 2
                        + g2d.getFontMetrics().getAscent();
                g2d.drawString(cellData, x, y);
            }
        }
    }

    public void drawRuntime(Graphics2D g2d, Rectangle rect, boolean showPaint) {
        if(!showPaint) {
            return;
        }
        g2d.setColor(GRAY);
        g2d.fill(rect);
        FontMetrics metrics = g2d.getFontMetrics();
        String runtime = formatTime(System.currentTimeMillis() - startTime);
        int x = rect.x + (rect.width - metrics.stringWidth(runtime)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setColor(Color.WHITE);
        g2d.drawString(runtime, x, y);
        g2d.draw(rect);
    }

    private String formatTime(final long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60;
        m %= 60;
        h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private String formatNumber(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        }
        int numKs = number / 1000;
        int hundreds = (number - numKs * 1000) / 100;
        return String.format("%d.%dk", numKs, hundreds);
    }


    @Override
    public void checkMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
            Point clickPt = mouseEvent.getPoint();
            if (new Rectangle(0, 0, cellWidth, cellHeight).contains(clickPt)) {
                showPaint = !showPaint;
                mouseEvent.consume();
            }
        }
    }

    private void drawMouse(Graphics2D g) {
        Point mP = script.getMouse().getPosition();
        g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);
    }
}
