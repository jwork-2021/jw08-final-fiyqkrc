package game.graphic.effect;

import com.pFrame.PTimerTask;
import com.pFrame.Pixel;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

public class Hit extends Effect{
    public static Pixel[][] HitImage= GraphicItemGenerator.generateItem("image/effect/hit.png", World.tileSize,World.tileSize).getPixels();

    public Hit(){
        super();
        graphic=HitImage;
        width=World.tileSize;
        height=World.tileSize;

        repeat=false;
        time=100;
        task=new Task(this);
    }

    class Task implements PTimerTask{
        public Effect effect;

        public Task(Effect effect){
            this.effect=effect;
        }

        @Override
        public void doTask() {
            world.removeItem(effect);
        }
    }
}
