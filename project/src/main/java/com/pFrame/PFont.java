package com.pFrame;

import imageTransFormer.GraphicItemGenerator;

public class PFont {

    public static Pixel[][] fontImagePixels;
    private static final Pixel[][][] fontsPixels = new Pixel[256][][];
    public static int fontBaseSize = 10;

    static {
        fontImagePixels = GraphicItemGenerator.generateItem("cp437_10x10.png", 16*PFont.fontBaseSize, 16*PFont.fontBaseSize).getPixels();
    }

    public static Pixel[][] getCharByPixels(char ch) {
        return PFont.getCharByPixels((int) ch);
    }

    public static Pixel[][] getCharByPixels(int ch) {
        if (fontsPixels[ch] != null) {
            return Pixel.pixelsCopy(fontsPixels[ch]);
        } else {
            Pixel[][] res = Pixel.subPixels(fontImagePixels, Position.getPosition(ch / 16 * PFont.fontBaseSize, (ch % 16) * PFont.fontBaseSize), PFont.fontBaseSize, PFont.fontBaseSize);
            for (int i = 0; i < fontBaseSize; i++) {
                for (int j = 0; j < fontBaseSize; j++) {
                    if (res[i][j] != null && (res[i][j].getColor().getRGB() & 0x00ffffff) == 0x00000000) {
                        res[i][j] = null;
                    }
                }
            }
            fontsPixels[ch] = res;
            return Pixel.pixelsCopy(res);
        }
    }
}
