package game.graphic.creature.monster;

import com.pFrame.Position;
import game.Location;
import game.world.World;

public class Master extends Monster {

    public Master() {
        super("image/monster/Master", World.tileSize, World.tileSize);
        health = 500;
        attack = 20;
        resistance = 0.2;
        speed = 4;
        speedLimit = speed;
        healthLimit = health;
        resistanceLimit = resistance;
        attackLimit = attack;
        coldTime = 12000;
        attackRange=2;
    }

    @Override
    public void responseToEnemy() {
        super.responseToEnemy();
        aim = searchAim();
        if (aim != null) {
            int x = this.world.getTileByLocation(getCentralPosition()).x();
            int y = this.world.getTileByLocation(getCentralPosition()).y();
            for (int i = x - 2; i <= x + 2; i++) {
                for (int j = y - 2; j <= y + 2; j++) {
                    if (!getWorld().locationOutOfBound(new Location(i, j)) && world.findThing(new Location(i, j)) == null) {
                        Vine vine = new Vine(this, Position.getPosition(i * World.tileSize, j * World.tileSize));
                        world.addItem(vine);
                    }
                }
            }
        }
    }
}
