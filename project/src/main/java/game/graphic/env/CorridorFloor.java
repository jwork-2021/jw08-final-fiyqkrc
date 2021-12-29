package game.graphic.env;

import com.alibaba.fastjson.JSONObject;
import com.pFrame.Pixel;
import game.graphic.StatedSavable;
import game.graphic.Thing;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

import java.util.Random;

public class CorridorFloor extends Thing implements StatedSavable {
    static public String[] FloorPaths = {
            "image/source/1-45.png"
    };
    static public Pixel[][][] AllPixels;

    static {
        AllPixels=new Pixel[FloorPaths.length][][];
        AllPixels[0]= GraphicItemGenerator.generateItem(FloorPaths[0], World.tileSize,World.tileSize).getPixels();
    }

    public CorridorFloor(){
        super(null);
        Random random=new Random();
        graphic=AllPixels[random.nextInt(FloorPaths.length)];
        height=World.tileSize;
        width=World.tileSize;
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
