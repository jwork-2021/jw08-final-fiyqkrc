package game.graphic;

import com.pFrame.Pixel;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

public class Tombstone extends Thing {
    public static Pixel[][] tombImage = GraphicItemGenerator.generateItem("image/deadRole.png", World.tileSize * 3 / 4, World.tileSize).getPixels();

    public Tombstone() {
        super(null);
        graphic = tombImage;
        width = World.tileSize * 3 / 4;
        height = World.tileSize;
        beCoverAble = true;
    }
}
