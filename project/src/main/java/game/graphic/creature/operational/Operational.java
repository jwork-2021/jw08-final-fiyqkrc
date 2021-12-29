package game.graphic.creature.operational;

import com.pFrame.Position;
import game.graphic.creature.Creature;
import game.world.World;

import java.util.Random;

abstract public class Operational extends Creature {

    public Operational(String path, int width, int height) {
        super(path, width, height);
        group = 2;
        speed = speed * 2;
    }

    @Override
    public void deHealth(double i) {
        super.deHealth(i);
        if (world != null) {
            if (world.screen != null && world.getControlRole() == this) {
                world.screen.displayHealth(health, healthLimit);
            }
            if (isDead())
                dead();
        }
    }

    @Override
    public void addCoin(int n) {
        super.addCoin(n);
        if (world.screen != null && world.getControlRole() == this) {
            world.screen.setCoinValue(coin);
        }

    }

    @Override
    public void dead() {
        super.dead();
        if (!World.multiPlayerMode) {
            if (world.getControlRole() == this)
                world.gameFinish();
        } else {
            if (World.mainClient) {
                this.deHealth(-healthLimit / 2);
                this.coin = 0;
                while (true) {
                    int x = new Random().nextInt(world.getWidth());
                    int y = new Random().nextInt(world.getHeight());
                    if (world.isLocationReachable(this, Position.getPosition(y, x))) {
                        setPosition(Position.getPosition(y - height / 2, x - width / 2));
                        if (world.addItem(this))
                            break;
                    }
                }
            }
        }
    }
}
