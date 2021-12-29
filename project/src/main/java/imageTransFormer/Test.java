package imageTransFormer;

import com.pFrame.Pixel;
import com.pFrame.pgraphic.PGraphicItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        PGraphicItem item = GraphicItemGenerator.generateItem(Test.class.getClassLoader().getResource("image/role/oldMan/2.png").getFile(), 32, 32);
        try {
            //BufferedImage image=Pixel.toBufferedImage(Pixel.valueOf(ObjectTransFormer.Rotate(Pixel.toBufferedImage(item.getPixels()),270)));
            BufferedImage image=Pixel.toBufferedImage(Rotate.rotate(item.getPixels()));
            ImageIO.write(image, "png",new FileOutputStream(new File("/home/fiyqkrc/6.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Rotate{
    public static Pixel[][] rotate(Pixel[][] pixels){
        Pixel[][] pixel=Pixel.emptyPixels(pixels.length,pixels[0].length);
        for(int i=0;i<pixels.length;i++){
            for(int j=0;j<pixels[0].length;j++){
                pixel[i][j]=pixels[i][pixels[0].length-j-1];
            }
        }
        return pixel;
    }
}
