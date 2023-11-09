package Paint;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.canvas.paint.Painter;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.script.Script;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ScriptPaint extends BotMouseListener implements Painter {
    private static final Color GRAY = new Color(70, 61, 50, 156);
    private final static Rectangle TOGGLE_PAINT_VISIBILITY = new Rectangle(0, 291, 47, 47);
    private boolean paintVisible = false;
    private final Script script;
    private final long startTime;
    private final ExperienceTracker tracker;

    private final int startLvlFishing;
    private final int startLvlStr;
    private final int startLvlAgility;

    String[][] data = {
            {"", "+XP (XP/H)", "LVL (+)"},
            {"Fishing", "", ""},
            {"Strength", "", ""},
            {"Agility", "", ""}
    };

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
        drawExcelGrid(g2d, data, 100, 50, new Point(0, 0));
    }

    private void populateDataGrid() {
        data[1][1] = String.format("+%s (%s)", formatNumber(tracker.getGainedXP(Skill.FISHING)), formatNumber(tracker.getGainedXPPerHour(Skill.FISHING)));
        data[1][2] = String.format("%s (+%s)", startLvlFishing, tracker.getGainedLevels(Skill.FISHING));

        data[2][1] = String.format("+%s (%s)", formatNumber(tracker.getGainedXP(Skill.STRENGTH)), formatNumber(tracker.getGainedXPPerHour(Skill.STRENGTH)));
        data[2][2] = String.format("%s (+%s)", startLvlStr, tracker.getGainedLevels(Skill.STRENGTH));

        data[3][1] = "^";
        data[3][2] = String.format("%s (+%s)", startLvlAgility, tracker.getGainedLevels(Skill.AGILITY));
    }

    private void drawExcelGrid(Graphics2D g2d, String[][] data, int cellWidth, int cellHeight, Point startPoint) {
        int numRows = data.length;
        int numCols = data[0].length;
        int gridWidth = numCols * cellWidth;
        int gridHeight = numRows * cellHeight;

        g2d.setColor(GRAY);
        g2d.fillRect(startPoint.x, startPoint.y, gridWidth, gridHeight);

        g2d.setColor(Color.WHITE);

        for (int i = 0; i <= numRows; i++) {
            int y = startPoint.y + i * cellHeight;
            g2d.drawLine(startPoint.x, y, startPoint.x + gridWidth, y);
        }

        for (int i = 0; i <= numCols; i++) {
            int x = startPoint.x + i * cellWidth;
            g2d.drawLine(x, startPoint.y, x, startPoint.y + gridHeight);
        }

        Font font = new Font("Arial", Font.PLAIN, 14);
        g2d.setFont(font);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                String cellData = data[i][j];
                int x = startPoint.x + j * cellWidth + (cellWidth - g2d.getFontMetrics().stringWidth(cellData)) / 2;
                int y = startPoint.y + i * cellHeight + (cellHeight - g2d.getFontMetrics().getHeight()) / 2
                        + g2d.getFontMetrics().getAscent();
                g2d.drawString(cellData, x, y);
            }
        }
    }

    private String formatNumber(int number) {
        if(number < 1000) {
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
            if (TOGGLE_PAINT_VISIBILITY.contains(clickPt)) {
                paintVisible = !paintVisible;
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
