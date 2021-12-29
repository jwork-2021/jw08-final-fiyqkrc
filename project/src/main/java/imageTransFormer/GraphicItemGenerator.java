package imageTransFormer;

import com.pFrame.Pixel;
import com.pFrame.pgraphic.PGraphicItem;

import java.awt.image.*;
import java.io.*;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

public class GraphicItemGenerator {

    private static final HashMap<String, BufferedImage> source = new HashMap<>();

    public static PGraphicItem generateItem(String absPath, int width, int height) {

        BufferedImage image = null;
        try {
            synchronized (source) {
                if (source.containsKey(absPath)) {
                    image = source.get(absPath);
                } else {
                    InputStream inputStream=Objects.requireNonNull(GraphicItemGenerator.class.getClassLoader().getResourceAsStream(absPath));
                    image = ImageIO.read(inputStream);
                    inputStream.close();
                    source.put(absPath,image);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not exist");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null) {
            Pixel[][] pixels = ObjectTransFormer.transform(image, width, height);
            PGraphicItem item = new PGraphicItem(pixels);
            return item;
        } else {
            return null;
        }
    }
}
