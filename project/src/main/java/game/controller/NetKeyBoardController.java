
package game.controller;

import com.pFrame.pwidget.PFrameKeyListener;
import game.graphic.Direction;
import game.graphic.creature.Controllable;
import game.graphic.interactive.GameThread;
import game.server.client.Accepter;
import game.server.client.ClientMain;
import game.world.World;
import log.Log;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetKeyBoardController extends CreatureController implements PFrameKeyListener, Runnable {

    Thread thread;

    public NetKeyBoardController() {
        super();
        allKeyCode.add('a');
        allKeyCode.add('s');
        allKeyCode.add('w');
        allKeyCode.add('d');

    }

    public void setThing(Controllable controllable) {
        this.controllable = controllable;
    }

    public ArrayList<Character> allKeyCode = new ArrayList<>();

    final CopyOnWriteArrayList<Character> keyArray = new CopyOnWriteArrayList<>();


    @Override
    public void keyPressed(KeyEvent e) {

        synchronized (keyArray) {
            if (allKeyCode.contains(e.getKeyChar())) {
                if (keyArray.contains(e.getKeyChar())) {
                } else
                    keyArray.add(e.getKeyChar());
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'j')
            ClientMain.getInstance().getCommandListener().submit(Accepter.attackMessage(controllable));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        synchronized (keyArray) {
            if (allKeyCode.contains(e.getKeyChar())) {
                if (keyArray.contains(e.getKeyChar()))
                    keyArray.remove((Character) e.getKeyChar());
            }
        }
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (keyArray.size() == 0) {

                } else if (keyArray.size() == 1) {
                    ClientMain.getInstance().getCommandListener().submit(Accepter.MoveMessage(controllable, calDirection(keyArray.get(0))));
                } else {
                    double d1 = calDirection(keyArray.get(0));
                    double d2 = calDirection(keyArray.get(1));
                    ClientMain.getInstance().getCommandListener().submit(Accepter.MoveMessage(controllable, (d1 + d2) / 2));
                }
                Thread.sleep(33);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GameThread.threadSet.remove(Thread.currentThread());
    }

    public double calDirection(char ch) {
        double direction = 0;
        switch (ch) {
            case 'w' -> direction = Direction.Up;
            case 'a' -> direction = Direction.Left;
            case 'd' -> direction = Direction.Right;
            case 's' -> direction = Direction.Down;
            default -> {
            }
        }
        return direction;
    }

    @Override
    public void stop() {
        if (controllable != null) {
            World world = controllable.getWorld();
            if (world != null) {
                world.freeKeyListener('w', this);
                world.freeKeyListener('a', this);
                world.freeKeyListener('s', this);
                world.freeKeyListener('d', this);
                world.freeKeyListener('j', this);
            } else {
                Log.WarningLog(this, "world is null");
            }
        } else {
            Log.WarningLog(this, "controllable is null");
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public void start() {
        if (controllable != null) {
            World world = controllable.getWorld();
            if (world != null) {
                world.addKeyListener('w', this);
                world.addKeyListener('a', this);
                world.addKeyListener('s', this);
                world.addKeyListener('d', this);
                world.addKeyListener('j', this);
            } else {
                Log.WarningLog(this, "world is null");
            }
        } else {
            Log.WarningLog(this, "controllable is null");
        }
        if (thread != null) {
            stop();
        }
        thread = new Thread(this);
        thread.start();
        GameThread.threadSet.add(thread);
    }
}
