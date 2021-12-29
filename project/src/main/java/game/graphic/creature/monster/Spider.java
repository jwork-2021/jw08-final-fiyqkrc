package game.graphic.creature.monster;

import game.graphic.Direction;
import game.graphic.bullet.SpiderShoot;
import game.world.World;

public class Spider extends Monster {

    public Spider() {
        super("image/monster/Spider", World.tileSize, World.tileSize);
        attack = 20;
        speed = 3;
        health = 45;
        resistance = 0.1;
        speedLimit = speed;
        healthLimit = health;
        resistanceLimit = resistance;
        attackLimit = attack;
        coldTime = 300;
        attackRange=7;
    }

    @Override
    public void responseToEnemy() {
        super.responseToEnemy();
        aim = searchAim();
        if (aim != null) {
            try {
                direction = Direction.calDirection(getCentralPosition(), aim.getCentralPosition());
                SpiderShoot shoot = new SpiderShoot(this, direction);
                world.addItem(shoot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

