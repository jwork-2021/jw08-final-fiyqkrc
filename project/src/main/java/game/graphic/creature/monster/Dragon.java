package game.graphic.creature.monster;

import game.graphic.Direction;
import game.graphic.bullet.Wind;
import game.world.World;

public class Dragon extends Monster {

    public Dragon() {
        super("image/monster/Dragon", World.tileSize, World.tileSize);
        speed = 2;
        health = 400;
        resistance = 0.4;
        attack = 70;
        speedLimit = speed;
        healthLimit = health;
        resistanceLimit = resistance;
        attackLimit = attack;
        coldTime = 3000;
        attackRange = 5;
    }

    @Override
    public void responseToEnemy() {
        super.responseToEnemy();
        try {
            aim = searchAim();
            if (aim != null) {
                direction = Direction.calDirection(getCentralPosition(), aim.getCentralPosition());
                Wind wind = new Wind(this, direction);
                world.addItem(wind);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
