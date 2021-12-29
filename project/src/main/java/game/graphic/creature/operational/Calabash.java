package game.graphic.creature.operational;

import game.Location;
import game.graphic.Direction;
import game.graphic.bullet.NormalBullet;
import game.world.World;

public class Calabash extends Operational {

    public Calabash() {
        super("image/role/calabash0", World.tileSize, World.tileSize);
        health = 500;
        healthLimit = 500;
        attack = 12;
        attackLimit = 12;
        resistance = 0.2;
        resistanceLimit = 0.2;
        speed = 4;
        speedLimit = 4;
    }

    @Override
    public void responseToEnemy() {
        Location l = world.searchNearestEnemy(this, 5);
        double angle;
        if (l == null) {
            angle = this.direction;
        } else {
            angle = Direction.calDirection(getCentralPosition(), l.getCentralPosition());
        }
        NormalBullet normalBullet = new NormalBullet(this, angle);
        world.addItem(normalBullet);

    }

}
