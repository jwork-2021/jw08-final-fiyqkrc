package com.pFrame.pwidget;

import com.pFrame.PFont;
import com.pFrame.Pixel;
import com.pFrame.Position;
import java.awt.Color;

public class PLabel extends PWidget {
    protected String text = "";
    protected Color color = Color.WHITE;
    protected Pixel[][] content;
    protected int fontScale = 1;

    public PLabel(PWidget parent, Position p) {
        super(parent, p);
        this.content = Pixel.emptyPixels(this.getWidgetWidth(), this.getWidgetHeight());
        this.fontScale = 1;
        this.text = "";
    }

    @Override
    public Pixel[][] displayOutput() {
        pixels=super.displayOutput();
        if (this.getWidgetHeight() <= 0 || this.getWidgetWidth() <= 0) {
            return pixels;
        } else {
            return Pixel.pixelsAdd(pixels, this.content, Position.getPosition(0, 0));
        }
    }

    public void setText(String text, int size, Color color) {
        this.text = text;
        if (color != null)
            this.color = color;
        this.fontScale = size;
        content=Pixel.emptyPixels(widgetWidth,widgetHeight);
        this.updateDraw();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        this.content = Pixel.emptyPixels(this.getWidgetWidth(), this.getWidgetHeight());
        this.updateDraw();
    }

    protected void updateDraw() {
        if (this.content != null && this.fontScale != 0 && this.color != null) {
            int line = Math.max(1, (this.getWidgetHeight() / (PFont.fontBaseSize * this.fontScale)));
            int charsInLine = Math.max(1, (this.getWidgetWidth() / (PFont.fontBaseSize * this.fontScale)));
            int chIndex = 0;
            for (int i = 0; i < line; i++) {
                for (int j = 0; j < charsInLine; j++) {
                    if (chIndex < this.text.length()) {
                        if(text.charAt(chIndex)=='\n'){
                            i++;
                            j=0;
                        }
                        else if(text.charAt(chIndex)=='\t'){
                            j+=2;
                        }
                        else {
                            Pixel[][] fontPixels = PFont.getCharByPixels(this.text.charAt(chIndex));
                            fontPixels = Pixel.pixelsScaleLarger(fontPixels, this.fontScale);
                            fontPixels = Pixel.pixelsSetColor(fontPixels, this.color);
                            Pixel.pixelsAdd(this.content, fontPixels, Position
                                    .getPosition(PFont.fontBaseSize * i * fontScale, PFont.fontBaseSize * j * fontScale));
                        }
                        chIndex++;
                    }
                }
            }
        }
    }
}
