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
    private final static Rectangle TOGGLE_PAINT_VISIBILITY = new Rectangle(0, 291, 47, 47);
    private boolean paintVisible = false;
    private final Script script;
    private final long startTime;
    private final ExperienceTracker tracker;

    private final int startLvlFishing;
    private final int startLvlStr;
    private final int startLvlAgility;

    private Rectangle ReportBtnBox;

    private Point xpTableOrigin;

    private final String[][] data = {
            {"", "+XP (XP/H)", "LVL (+)"},
            {"Fishing", "", ""},
            {"Strength", "", ""},
            {"Agility", "", ""}
    };

    private final int cellWidth = 100;
    private final int cellHeight = 50;

    public ScriptPaint(Script script) {
        this.script = script;

        RS2Widget reportBtn = script.widgets.get(162, 31);
        ReportBtnBox = reportBtn.getBounds();

        RS2Widget rsLog = script.widgets.get(162, 53);
        int debug = rsLog.getBounds().y - data.length * cellHeight;
        script.log("debug: " + debug);
        xpTableOrigin = new Point(0, rsLog.getBounds().y - data.length * cellHeight);

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
        drawExcelGrid(g2d, data, cellWidth, cellHeight, xpTableOrigin);
        drawRuntime(g2d, ReportBtnBox);
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

    public void drawRuntime(Graphics2D g2d, Rectangle rect) {
        g2d.setColor(new Color(235, 25, 25, 156));
        g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
        FontMetrics metrics = g2d.getFontMetrics();
        String runtime = String.valueOf(formatTime(System.currentTimeMillis() - startTime));
        int x = rect.x + (rect.width - metrics.stringWidth(runtime)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setColor(Color.WHITE);
        g2d.drawString(runtime, x, y);
    }

    private String formatTime(final long ms){
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60; m %= 60; h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
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
