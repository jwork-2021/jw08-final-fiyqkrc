package game.screen;

import com.pFrame.PFont;
import com.pFrame.Pixel;
import com.pFrame.Position;
import com.pFrame.pwidget.PWidget;
import log.Log;

import java.awt.*;
import java.util.ArrayList;

public class HealthBar extends PWidget {
    protected Pixel[][] pixels;
    protected double health;
    protected double healthLimit;

    protected Position drawArea;
    protected int drawHeight;
    protected int drawWidth;

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        updatePixels();
    }

    public void updatePixels() {
        String text = String.format("health: %d / %d", (int) health, (int) healthLimit);
        if (widgetHeight <= PFont.fontBaseSize) {
            Log.WarningLog(this, "health widgetBar height too small:" + widgetHeight + ", will display nothing");
            return;
        }
        if (widgetWidth < text.length() * PFont.fontBaseSize) {
            if (widgetWidth > String.format("%d/%d", (int) health, (int) healthLimit).length() * PFont.fontBaseSize) {
                text = String.format("%d/%d", (int) health, (int) healthLimit);
            } else {
                Log.WarningLog(this, "health widgetBar width too small:" + widgetWidth + ", will display nothing");
                return;
            }
        }
        pixels = Pixel.emptyPixels(widgetWidth, widgetHeight);
        drawArea = Position.getPosition(9, 1);
        drawHeight = Math.min(10, widgetHeight - 10);
        drawWidth = widgetWidth - 2;
        for (int i = 0; i < text.length(); i++) {
            Pixel.pixelsAdd(pixels, PFont.getCharByPixels(text.charAt(i)), Position.getPosition(0, PFont.fontBaseSize * i));
        }
        int drawLength = (int) (health / healthLimit * drawWidth);
        for (int i = 0; i < drawHeight; i++) {
            for (int j = 0; j < drawLength; j++) {
                pixels[drawArea.getX() + i][drawArea.getY() + j] = Pixel.getPixel(Color.green, (char) 0xf0);
            }
        }
    }

    public HealthBar(PWidget parent, Position p) {
        super(parent, p);
        pixels = Pixel.emptyPixels(widgetWidth, widgetHeight);
    }

    public void display(double health, double healthLimit) {
        this.health = health;
        this.healthLimit = healthLimit;
        updatePixels();
    }

    @Override
    public Pixel[][] displayOutput() {
        Pixel[][] graphic = super.displayOutput();
        Pixel.pixelsAdd(graphic, pixels, Position.getPosition(0, 0));
        return graphic;
    }

    @Override
    public ArrayList<PWidget> getChildWidget() {
        ArrayList<PWidget> pWidgets = new ArrayList<>();
        pWidgets.add(this);
        return pWidgets;
    }

    @Override
    public ArrayList<PWidget> getWidgetsAt(Position p) {
        ArrayList<PWidget> pWidgets = new ArrayList<>();
        pWidgets.add(this);
        return pWidgets;
    }
}
