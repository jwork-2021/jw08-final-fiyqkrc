package game.graphic.drop.buff;

import game.world.World;
import imageTransFormer.GraphicItemGenerator;

public class ResistanceBuff extends Buff {
    public ResistanceBuff(){
        image= GraphicItemGenerator.generateItem("image/effect/buff/2-2.png", World.tileSize,World.tileSize).getPixels();
        graphic=image;
        width= World.tileSize;
        height=World.tileSize;

        health=0;
        attack=0;
        resistance=0.3;
        speed=0;
        timeOnly=true;
        time=60000;
    }
}
