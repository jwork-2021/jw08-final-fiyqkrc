package game.controller;

import com.pFrame.Position;
import game.Config;
import game.graphic.Direction;
import game.graphic.creature.Creature;
import game.graphic.interactive.GameThread;
import game.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class AlgorithmController extends CreatureController implements Runnable {
    protected Creature aim;
    protected double direction;
    protected double lastSearchAim = System.currentTimeMillis();
    protected Random random = new Random();
    protected Thread thread;
    protected boolean lastMoveSuccess;
    protected long lastAttack;


    public AlgorithmController() {
        lastMoveSuccess = true;
    }

    public void tryMove() {

        if (random.nextDouble(1) > 0.8) {
            direction = random.nextDouble(Math.PI * 2);
        } else {
            controllable.move(direction);
        }
    }

    public void trySearchAim() {
        if (System.currentTimeMillis() - lastSearchAim > 2000) {
            aim = controllable.searchAim();
            lastSearchAim = System.currentTimeMillis();
        }
    }

    public void stop() {
        thread.interrupt();
        thread=null;
    }

    @Override
    public void start() {
        if(thread!=null){
            stop();
        }
        thread = new Thread(this);
        GameThread.threadSet.add(thread);
        thread.start();
    }

    @Override
    public void run() {
        Thread dataAnalysis = new Thread(new DataAnalysis(this));
        dataAnalysis.start();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (controllable.isDead()) {
                    controllable.dead();
                    break;
                }

                if (aim == null) {
                    trySearchAim();
                    tryMove();
                } else if (Position.distance(aim.getCentralPosition(), controllable.getCentralPosition()) > controllable.getAttackRange() * World.tileSize) {
                    if (Position.distance(aim.getCentralPosition(), controllable.getCentralPosition()) > World.tileSize * 10) {
                        aim = null;
                    } else {
                        double direction = Direction.calDirection(controllable.getCentralPosition(), aim.getCentralPosition());
                        controllable.move(direction);
                    }
                } else if (System.currentTimeMillis() - lastAttack > controllable.getColdTime()) {
                    controllable.responseToEnemy();
                    lastAttack = System.currentTimeMillis();
                } else {
                    tryMove();
                }

                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                aim = null;
                direction = 0;
            }
        }
        dataAnalysis.interrupt();
        GameThread.threadSet.remove(Thread.currentThread());
    }


}

class DataAnalysis implements Runnable {

    public static FileOutputStream stream;

    AlgorithmController controller;

    static {
        try {
            File file;
            file = new File(Config.LearningDataPath + "/data.txt");
            if (!file.exists())
                file.createNewFile();
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeInfo(String str) {
        try {
            stream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataAnalysis(AlgorithmController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(2000);
                if (controller.aim != null) {
                    Creature me = ((Creature) controller.controllable);
                    Creature enemy = ((Creature) controller.aim);
                    //health,distance,enemy health,nenmy attack
                    String str = String.format("%f\t%f\t%f\t%f\n", me.getHealth(), Position.distance(me.getCentralPosition(), enemy.getCentralPosition()),
                            enemy.getHealth(), enemy.getAttack());
                    writeInfo(str);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
