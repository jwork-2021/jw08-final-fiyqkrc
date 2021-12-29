package game.graphic.env;

import com.alibaba.fastjson.JSONObject;
import com.pFrame.Pixel;
import game.graphic.StatedSavable;
import game.graphic.Thing;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;
import java.util.Random;

public class Door extends Thing implements StatedSavable {
    static public String[] DoorPaths = {
            "image/source/0-39.png"
    };
    static public Pixel[][][] AllPixels;

    static {
        AllPixels=new Pixel[DoorPaths.length][][];
        AllPixels[0]= GraphicItemGenerator.generateItem(DoorPaths[0], World.tileSize,World.tileSize).getPixels();
    }

    public Door(){
        super(null);
        Random random=new Random();
        graphic=AllPixels[random.nextInt(DoorPaths.length)];
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
