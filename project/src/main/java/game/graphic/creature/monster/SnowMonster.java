package game.graphic.creature.monster;

import com.pFrame.Position;
import game.Location;
import game.world.World;

public class SnowMonster extends Monster {

    public SnowMonster() {
        super("image/monster/SnowMonster", World.tileSize, World.tileSize);
        health = 400;
        attack = 20;
        speed = 2;
        resistance = 0.1;
        speedLimit = speed;
        healthLimit = health;
        resistanceLimit = resistance;
        attackLimit = attack;
        coldTime = 10000;
        attackRange=2;
    }

    @Override
    public void responseToEnemy() {
        aim = searchAim();
        if (aim != null) {
            if ((Math.abs(p.getX() - aim.getPosition().getX()) > World.tileSize || Math.abs(p.getY() - aim.getPosition().getY()) > World.tileSize))
                tryMoveToEnemy();
            else {
                int x = this.world.getTileByLocation(getCentralPosition()).x();
                int y = this.world.getTileByLocation(getCentralPosition()).y();
                for (int i = x - 1; i <= x + 1; i++) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (!getWorld().locationOutOfBound(new Location(i, j))) {
                            IceAttack iceAttack = new IceAttack(this, Position.getPosition(i * World.tileSize, j * World.tileSize));
                            world.addItem(iceAttack);
                        }
                    }
                }
            }
        }
    }
}
