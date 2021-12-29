package game.graphic.drop;

import com.pFrame.Pixel;
import com.pFrame.Position;
import game.graphic.Direction;
import game.graphic.Thing;
import game.graphic.creature.Creature;
import game.graphic.creature.operational.Operational;
import game.graphic.interactive.GameThread;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

public class Coin extends Thing implements Runnable {
    double last_x;
    double last_y;
    long lastFlashTime;
    double speed;
    int coin;
    Thread thread;

    public static Pixel[][] coinImage = GraphicItemGenerator.generateItem("image/effect/coin.png", World.tileSize, World.tileSize).getPixels();


    public Coin(Creature creature) {
        super(null);
        this.graphic = coinImage;
        this.p = creature.getPosition();
        this.width = World.tileSize;
        this.height = World.tileSize;
        this.coin = creature.getCoin();
        this.speed = 200;
    }

    @Override
    public void run() {
        lastFlashTime = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (Operational operational : world.getOperational()) {
                    if (Position.distance(getCentralPosition(), operational.getCentralPosition()) < 7 * World.tileSize) {
                        double angle = Direction.calDirection(getCentralPosition(), operational.getCentralPosition());
                        long currentTime = System.currentTimeMillis();
                        double y = Math.sin(angle) * speed * (currentTime - lastFlashTime) / 1000 + last_y;
                        double x = Math.cos(angle) * speed * (currentTime - lastFlashTime) / 1000 + last_x;
                        last_y = y - (int) y;
                        last_x = x - (int) x;
                        lastFlashTime = currentTime;
                        Position nextPosition = Position.getPosition(getCentralPosition().getX() - (int) y, getCentralPosition().getY() + (int) x);
                        world.ThingMove(this, nextPosition);
                        Thing thing = world.findThing(world.getTileByLocation(nextPosition));
                        if (thing instanceof Operational) {
                            ((Operational) thing).addCoin(coin);
                            world.removeItem(this);
                            Thread.currentThread().interrupt();
                            break;
                        }
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
}
