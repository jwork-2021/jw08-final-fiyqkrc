package com.pFrame;

import com.pFrame.pgraphic.PGraphicItem;
import com.pFrame.pwidget.PImage;
import imageTransFormer.ObjectTransFormer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Pixel {
    private Color color;
    private final char ch;

    private Pixel(Color color, char ch) {
        this.ch = ch;
        this.color = color;
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public char getCh() {
        return this.ch;
    }

    public Pixel copy() {
        return new Pixel(color, ch);
    }



    public static Pixel getPixel(Color color, char ch) {
        return new Pixel(color, ch);
    }


    /**
     * add the pixel of dest to src
     * @param src the src pixel array
     * @param dest the dest pixel array
     * @param position  the start position of copy
     * @return  return the src pixel array after add operation
     */
    static public Pixel[][] pixelsAdd(Pixel[][] src, Pixel[][] dest, Position position) {
        if (src == null) {
            return null;
        } else if (dest == null) {
            return src;
        } else {
            int h = dest.length;
            int w = dest[0].length;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {

                    try{
                        if(dest[i][j]!=null){
                            src[position.getX()+i][position.getY()+j]=dest[i][j];
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        if(position.getX()+i<0) {
                            i = -position.getX();
                            i--;
                            break;
                        }
                        else if(position.getX()+i>=src.length){
                            h=src.length-position.getX();
                            i--;
                            break;
                        }
                        else if(position.getY()+j<0)
                        {
                            j=-position.getY();
                            j--;
                        }
                        else{
                            w=src[0].length-position.getY();
                            j--;
                        }
                    }
                }
            }
            return src;
        }
    }

    static public void pixelsClean(Pixel[][] pixels){
        if(pixels==null)
        {}
        else{
            for(int i=0;i<pixels.length;i++){
                for(int j=0;j<pixels[0].length;j++){
                    pixels[i][j]=null;
                }
            }
        }
    }

    static public Pixel[][] emptyPixels(int width, int height) {
        if(width>0&&height>0) {
            Pixel[][] pixels = new Pixel[height][width];
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++)
                    pixels[i][j] = null;
            return pixels;
        }
        else
            return null;
    }

    static public Pixel[][] valueOf(PGraphicItem item) {
        return item.getPixels();
    }

    static public Pixel[][] valueOf(PImage item) {
        return item.getPixels();
    }

    static public Pixel[][] subPixels(Pixel[][] pixels, Position p, int width, int height) {
        Pixel[][] res = Pixel.emptyPixels(width, height);
        if (pixels == null)
            return null;
        int h = pixels.length;
        int w = pixels[0].length;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                try {
                    if ((p.getX() + i < h && p.getX() + i >= 0) && (p.getY() + j < w && p.getY() + j >= 0)) {
                        res[i][j] = pixels[p.getX() + i][p.getY() + j];
                    }
                }
                catch (ArrayIndexOutOfBoundsException e){
                    if(p.getX()+i>=h){
                        height=h-p.getX();
                        i--;
                        break;
                    }
                    else if(p.getX()+i<0){
                        i=-p.getX();
                        i--;
                        break;
                    }
                    else if(p.getY()+j>=w){
                        width=w-p.getY();
                        j--;
                    }
                    else{
                        j=-p.getY();
                        j--;
                    }
                }
            }
        return res;
    }

    static  public Pixel[][] getPixelsScaleInstance(Pixel[][] pixels,int width,int height){
        BufferedImage image=Pixel.toBufferedImage(pixels);
        BufferedImage scaledImage= ObjectTransFormer.toBufferedImage(image.getScaledInstance(width,height,BufferedImage.SCALE_SMOOTH));
        return Pixel.valueOf(scaledImage);
    }

    static public Pixel[][] pixelsScaleLarger(Pixel[][] pixels, int scale) {
        if (pixels == null || scale == 1)
            return Pixel.pixelsCopy(pixels);
        else {
            int originHeight = pixels.length;
            int originWidth = pixels[0].length;
            Pixel[][] res = Pixel.emptyPixels(originWidth * scale, originHeight * scale);
            for (int i = 0; i < originHeight; i++) {
                for (int j = 0; j < originWidth; j++) {
                    for (int a = 0; a < scale; a++) {
                        for (int b = 0; b < scale; b++) {
                            res[i * scale + a][j * scale + b] = pixels[i][j];
                        }
                    }
                }
            }
            return res;
        }
    }

    static public Pixel[][] pixelsCopy(Pixel[][] pixels) {
        if (pixels == null)
            return null;
        else {
            int width = pixels[0].length;
            int height = pixels.length;
            Pixel[][] res = Pixel.emptyPixels(width, height);
            for (int i = 0; i < height; i++)
                System.arraycopy(pixels[i], 0, res[i], 0, width);
            return res;
        }
    }

    static public Pixel[][] pixelsSetColor(Pixel[][] pixels, Color color) {
        if (pixels == null || color == null)
            return null;
        else {
            int w = pixels[0].length;
            int h = pixels.length;
            for (int i = 0; i < h; i++)
                for (int j = 0; j < w; j++) {
                    if (pixels[i][j] != null)
                        pixels[i][j]=Pixel.getPixel(color,(char)0xf0);
                }
            return pixels;
        }
    }

    static public PGraphicItem toItem(Pixel[][] pixels) {
        return new PGraphicItem(pixels);
    }

    static public PImage toImage(Pixel[][] pixels) {
        return new PImage(null, null,pixels);
    }

    static public BufferedImage toBufferedImage(Pixel[][] pixels) {
        if (pixels == null) {
            return null;
        } else {
            int width = pixels[0].length;
            int height = pixels.length;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (pixels[i][j] != null) {
                        int rgb = 0;
                        Pixel pixel = pixels[i][j];
                        rgb = pixel.getColor().getRGB();
                        image.setRGB(j, i, rgb);
                    } else {
                        image.setRGB(j, i, 0x00000000);
                    }
                }
            }
            return image;
        }
    }

    static public Pixel[][] valueOf(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        } else {
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            Pixel[][] pixels = new Pixel[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if(bufferedImage.getRGB(j, i)>>24==0)
                        pixels[i][j]=null;
                    else
                        pixels[i][j] = Pixel.getPixel(new Color(bufferedImage.getRGB(j, i)), (char) 0xf0);
                }
            }
            return pixels;
        }
    }
}
