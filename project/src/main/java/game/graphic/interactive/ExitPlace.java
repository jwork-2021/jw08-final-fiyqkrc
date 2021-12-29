package game.graphic.interactive;

import com.alibaba.fastjson.JSONObject;
import com.pFrame.Pixel;
import com.pFrame.pwidget.PFrameKeyListener;
import game.graphic.StatedSavable;
import game.graphic.Thing;
import game.graphic.creature.operational.Operational;
import game.graphic.effect.Dialog;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

import java.awt.event.KeyEvent;

public class ExitPlace extends Thing implements Runnable, PFrameKeyListener, GameThread , StatedSavable {
    public static Pixel[][] exitImage = GraphicItemGenerator.generateItem("image/exit.png", World.tileSize, World.tileSize).getPixels();

    Thread thread;

    public ExitPlace() {
        super(null);
        graphic = exitImage;
        width = World.tileSize;
        height = World.tileSize;
        beCoverAble = true;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (world != null) {
                    Thing thing = world.findThing(getLocation());
                    if (thing instanceof Operational) {
                        Dialog dialog = new Dialog("Press f to exit maze", p, 3000);
                        world.addItem(dialog);
                        world.addKeyListener('f', this);
                    } else {
                        world.freeKeyListener('f', this);
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GameThread.threadSet.remove(Thread.currentThread());
    }


    @Override
    public void whenBeAddedToScene() {
        super.whenBeAddedToScene();
        thread = new Thread(this);
        thread.start();
        GameThread.threadSet.add(thread);
    }



    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'f' || e.getKeyChar() == 'F') {
            System.out.println("you choose to leave");
            world.gameFinish();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    @Override
    public Thread getThread() {
        return thread;
    }

    @Override
    public void stop() {
        thread.interrupt();
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
