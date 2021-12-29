package com.pFrame.pwidget;

import java.awt.event.MouseEvent;

import com.pFrame.Pixel;
import com.pFrame.Position;
import java.awt.Color;
import java.util.ArrayList;

import log.Log;

public class PButton extends PWidget {

    PLabel textLabel;
    Pixel[][] focusFrame;

    public PButton(PWidget parent, Position p) {

        super(parent, p);
        this.textLabel = new PLabel(this, Position.getPosition(0, 0));
        this.textLabel.changeWidgetSize(this.widgetWidth, this.widgetHeight);
        focusFrame = null;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (textLabel != null)
            this.textLabel.changeWidgetSize(this.widgetWidth, this.widgetHeight);
        if (focusFrame != null) {
            focusFrame = Pixel.emptyPixels(widgetWidth, widgetHeight);
            for (int i = 0; i < widgetHeight; i++) {
                for (int j = 0; j < widgetWidth; j++) {
                    if (i == 0 || i == widgetHeight - 1 || j == 0 || j == widgetWidth - 1)
                        focusFrame[i][j] = Pixel.getPixel(Color.white, (char) 0xf0);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e, Position p) {
        Log.InfoLog(this, "be click");
        super.mouseClicked(e, p);
    }

    @Override
    public Pixel[][] displayOutput() {
        Pixel[][] pixels = super.displayOutput();
        return Pixel.pixelsAdd(Pixel.pixelsAdd(pixels, this.textLabel.displayOutput(), this.textLabel.getPosition()),focusFrame,Position.getPosition(0, 0));
    }

    public void setText(String text, int size, Color color) {
        this.textLabel.setText(text, size, color);
    }

    @Override
    public ArrayList<PWidget> getWidgetsAt(Position p) {
        ArrayList<PWidget> res = new ArrayList<PWidget>();
        res.add(this);
        return res;
    }

    @Override
    public ArrayList<PWidget> getChildWidget() {
        return getWidgetsAt(null);
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        super.mouseEntered(arg0);
        focusFrame = Pixel.emptyPixels(widgetWidth, widgetHeight);
        for (int i = 0; i < widgetHeight; i++) {
            for (int j = 0; j < widgetWidth; j++) {
                if (i == 0 || i == widgetHeight - 1 || j == 0 || j == widgetWidth - 1)
                    focusFrame[i][j] = Pixel.getPixel(Color.white, (char) 0xf0);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        super.mouseExited(arg0);
        focusFrame=null;
    }
}
