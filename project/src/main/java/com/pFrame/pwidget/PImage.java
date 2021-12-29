package com.pFrame.pwidget;

import java.awt.*;
import java.util.Objects;

import com.pFrame.Pixel;
import com.pFrame.Position;

import imageTransFormer.GraphicItemGenerator;

public class PImage extends PWidget {


    protected Pixel[][] content;
    protected String path;

    public Pixel[][] getPixels() {
        return this.content;
    }

    public static PImage getPureImage(Color color) {
        Pixel[][] pixels = Pixel.emptyPixels(1, 1);
        pixels[0][0] = Pixel.getPixel(color, (char) 0xf0);
        return new PImage(null, null, pixels);
    }


    public PImage(PWidget parent, Position p, String absPath) {
        super(parent, p);
        this.content = null;
        this.path = absPath;
        load(path);
    }

    public PImage(PWidget parent, Position p, Pixel[][] pixels) {
        super(parent, p);
        this.content = pixels;
        this.path = null;
    }


    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (this.path != null) {
            this.load(path);
        } else if (this.content != null && this.widgetHeight != 0 && this.widgetWidth != 0) {
            this.content = Pixel.getPixelsScaleInstance(this.content, this.getWidgetWidth(), this.getWidgetHeight());
        }
    }

    public void load(String path) {
        if (path != null && this.getWidgetHeight() != 0 && this.getWidgetWidth() != 0) {
            this.content = Pixel
                    .valueOf(Objects.requireNonNull(GraphicItemGenerator.generateItem(path, this.getWidgetWidth(), this.getWidgetHeight())));
        }
    }

    @Override
    public Pixel[][] displayOutput() {
        Pixel[][] pixels = super.displayOutput();
        return Pixel.pixelsAdd(pixels, this.content, this.position);
    }
}