package game.controller;

import com.pFrame.Position;
import game.graphic.Direction;
import game.graphic.creature.Creature;
import game.graphic.interactive.GameThread;
import game.server.client.Accepter;
import game.server.client.ClientMain;
import game.world.World;

import java.util.Random;

public class NetAlController extends CreatureController implements Runnable {
    protected Creature aim;
    protected double direction;
    protected double lastSearchAim = System.currentTimeMillis();
    protected Random random = new Random();
    protected Thread thread;
    protected boolean lastMoveSuccess;
    protected long lastAttack;

    public NetAlController() {
        lastMoveSuccess = true;
    }

    public void tryMove() {

        if (random.nextDouble(1) > 0.8) {
            direction = random.nextDouble(Math.PI * 2);
        } else {
            ClientMain.getInstance().getCommandListener().submit(Accepter.MoveMessage(controllable, direction));
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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (controllable.isDead()) {
                    ClientMain.getInstance().getCommandListener().submit(Accepter.deadMessage(controllable));
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
                        ClientMain.getInstance().getCommandListener().submit(Accepter.MoveMessage(controllable, direction));
                    }
                } else if (System.currentTimeMillis() - lastAttack > controllable.getColdTime()) {
                    ClientMain.getInstance().getCommandListener().submit(Accepter.attackMessage(controllable));
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
        GameThread.threadSet.remove(Thread.currentThread());
    }
}