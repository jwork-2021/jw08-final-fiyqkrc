package game.graphic.effect;

import com.pFrame.PTimerTask;
import com.pFrame.Pixel;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

public class BulletHit extends Effect {
    public static Pixel[][] HitImage= GraphicItemGenerator.generateItem("image/effect/hit.png", World.tileSize/2,World.tileSize/2).getPixels();

    public BulletHit(){
        super();
        graphic=HitImage;
        width=World.tileSize/2;
        height=World.tileSize/2;

        repeat=false;
        time=100;
        task=new BulletHit.Task(this);
    }

    class Task implements PTimerTask {
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
