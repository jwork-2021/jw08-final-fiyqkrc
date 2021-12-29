package imageTransFormer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.pFrame.Pixel;

import log.Log;

public class ObjectTransFormer {

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, null, null);
        // bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static Pixel[][] transform(BufferedImage image, int width, int height) {
        if (image == null || width <= 0 || height <= 0) {
            Log.ErrorLog(ObjectTransFormer.class, "illgel input");
            return null;
        }

        BufferedImage bufferImage;
        if (width != image.getWidth() || height != image.getHeight()) {
            Image image2 = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            bufferImage = ObjectTransFormer.toBufferedImage(image2);
        } else {
            bufferImage = image;
        }

        Pixel[][] pixels = new Pixel[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((bufferImage.getRGB(j, i) >> 24) == 0)
                    pixels[i][j] = null;
                else
                    pixels[i][j] = Pixel.getPixel(new Color(bufferImage.getRGB(j, i)), (char) 0xf0);
            }
        }
        return pixels;
    }

    /**
     * 图片旋转
     * 
     * @param src   原始图
     * @param angel 旋转角度
     * @return 结果图字节数据组
     */
    public static BufferedImage Rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();
        g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
        g2.drawImage(src, null, null);
        return res;
    }

    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new java.awt.Rectangle(new Dimension(des_width, des_height));
    }

}
