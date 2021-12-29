package game.graphic.drop.buff;

import com.pFrame.Pixel;
import com.pFrame.Position;
import game.graphic.creature.operational.Operational;
import game.graphic.interactive.GameThread;
import game.graphic.Thing;
import game.graphic.creature.Creature;
import game.world.World;

import java.util.ArrayList;

public abstract class Buff extends Thing implements Runnable, GameThread {
    public Pixel[][] image;
    protected Thread thread;

    public double health = 0;
    public double attack = 0;
    public double resistance = 0;
    public double speed = 0;
    public boolean timeOnly = false;
    public int time = 0;
    public Creature creature = null;

    final static public ArrayList<Class<? extends Buff>> buffs = new ArrayList<>();

    static {
        buffs.add(AttackBuff.class);
        buffs.add(HealthBuff.class);
        buffs.add(ResistanceBuff.class);
        buffs.add(SpeedBuff.class);
    }

    public Buff() {
        super(null);
        image = null;
    }

    public Pixel[][] getImage() {
        return image;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (Operational operational : world.getOperational()) {
                    if (Position.distance(getCentralPosition(), operational.getCentralPosition()) < World.tileSize) {
                        Addition addition;
                        if (timeOnly)
                            addition = new Addition(this, creature, health, attack, speed, resistance, time);
                        else
                            addition = new Addition(this, creature, health, attack, speed, resistance);
                        operational.addAddition(addition);
                        world.removeItem(this);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        GameThread.threadSet.remove(Thread.currentThread());
    }

    @Override
    public void whenBeAddedToScene() {
        super.whenBeAddedToScene();
        thread = new Thread(this);
        GameThread.threadSet.add(thread);
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    @Override
    public Thread getThread() {
        return thread;
    }
}
