package imageTransFormer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import com.pFrame.Pixel;
import com.pFrame.pgraphic.PGraphicItem;

public class GraphicItemImageGenerator {
    public static void toImage(PGraphicItem item, String path) {
        BufferedImage image = new BufferedImage(item.getWidth(), item.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < item.getHeight(); i++) {
            for (int j = 0; j < item.getWidth(); j++) {
                if (item.getPixels()[i][j] != null) {
                    int rgb = 0;
                    Pixel pixel = item.getPixels()[i][j];
                    rgb = pixel.getColor().getRGB() + (pixel.getColor().getAlpha()<<24);
                    image.setRGB(j, i, rgb);
                } else {
                    image.setRGB(j, i, 0x00000000);

                }
            }
        }
        try {
            ImageIO.write(image, "png", new FileOutputStream(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
