package game.graphic.env;

import com.alibaba.fastjson.JSONObject;
import com.pFrame.Pixel;
import game.graphic.StatedSavable;
import game.graphic.Thing;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

import java.util.Random;

public class Wall extends Thing implements StatedSavable {
    static public String[] WallPaths = {
            "image/source/1-18.png",
            "image/source/1-15.png",
            "image/source/1-23.png"
    };
    static public Pixel[][][] AllPixels;

    static {
        AllPixels = new Pixel[WallPaths.length][][];
        AllPixels[0] = GraphicItemGenerator.generateItem(WallPaths[0], World.tileSize, World.tileSize).getPixels();
        AllPixels[1] = GraphicItemGenerator.generateItem(WallPaths[1], World.tileSize, World.tileSize).getPixels();
        AllPixels[2] = GraphicItemGenerator.generateItem(WallPaths[2], World.tileSize, World.tileSize).getPixels();

    }

    public Wall() {
        super(null);
        Random random = new Random();
        graphic = AllPixels[random.nextInt(WallPaths.length)];
        height = World.tileSize;
        width = World.tileSize;
        beCoverAble = false;
    }

    @Override
    public JSONObject saveState() {
        return save();
    }

    @Override
    public void resumeState(JSONObject jsonObject) {
        resume(jsonObject);
    }
}
