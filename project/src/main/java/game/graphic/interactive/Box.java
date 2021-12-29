package game.graphic.interactive;

import com.alibaba.fastjson.JSONObject;
import com.pFrame.Pixel;
import com.pFrame.Position;
import game.graphic.StatedSavable;
import game.graphic.Thing;
import game.graphic.drop.buff.Buff;
import game.graphic.creature.Creature;
import game.graphic.creature.operational.Operational;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

import java.util.Random;

public class Box extends Thing implements Runnable, GameThread, StatedSavable {
    public static Pixel[][] boxImage = GraphicItemGenerator.generateItem("image/effect/box/1.png", World.tileSize, World.tileSize).getPixels();

    protected Thread thread;

    protected boolean opened;

    public Box() {
        super(null);
        graphic = boxImage;
        width = World.tileSize;
        height = World.tileSize;
        beCoverAble = true;
        opened = false;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !opened) {
            try {
                if (world != null) {
                    Thing thing = world.findThing(getLocation());
                    if (thing instanceof Operational) {
                        graphic = GraphicItemGenerator.generateItem("image/effect/box/2.png", World.tileSize, World.tileSize).getPixels();
                        Random random = new Random();
                        Class<? extends Buff> buffClass = Buff.buffs.get(random.nextInt(Buff.buffs.size()));
                        Buff buff = buffClass.getDeclaredConstructor().newInstance();
                        buff.creature = (Creature) thing;
                        buff.setPosition(Position.getPosition(getPosition().getX() + random.nextInt(World.tileSize), getPosition().getY() + World.tileSize));
                        world.addItem(buff);
                        opened = true;
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
    public Thread getThread() {
        return thread;
    }

    @Override
    public void stop() {
        thread.interrupt();
    }


    @Override
    public JSONObject saveState() {
        JSONObject jsonObject = save();
        jsonObject.put("opened", opened);
        return jsonObject;
    }

    @Override
    public void resumeState(JSONObject jsonObject) {
        resume(jsonObject);
        opened = jsonObject.getObject("opened", Boolean.class);
        if (opened)
            graphic = GraphicItemGenerator.generateItem("image/effect/box/2.png", World.tileSize, World.tileSize).getPixels();
        else
            graphic = boxImage;
    }
}
