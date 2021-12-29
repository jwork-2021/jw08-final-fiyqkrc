package game.graphic.creature.monster;

import com.pFrame.Position;
import game.Attack;
import game.Location;
import game.graphic.Direction;
import game.graphic.Thing;
import game.graphic.creature.Creature;
import game.graphic.effect.Hit;
import game.graphic.effect.Swoon;
import game.world.World;

import java.util.ArrayList;

public class Pangolin extends Monster {

    protected double last_y;
    protected double last_x;


    public Pangolin() {
        super("image/monster/Pangolin", World.tileSize, World.tileSize);
        speed = 3;
        resistance = 0.5;
        attack = 40;
        health = 80;
        speedLimit = speed;
        healthLimit = health;
        resistanceLimit = resistance;
        attackLimit = attack;
        coldTime = 5000;
        attackRange=1;
    }

    @Override
    public void responseToEnemy() {
        super.responseToEnemy();
        try {
            aim = searchAim();
            if (aim != null) {
                direction = Direction.calDirection(getCentralPosition(), aim.getCentralPosition());
                double y = Math.sin(direction) * World.tileSize;
                double x = Math.cos(direction) * World.tileSize;

                last_x = x - (int) x;
                last_y = y - (int) y;

                Position nextPosition = Position.getPosition(p.getX() - (int) y, p.getY() + (int) x);
                Position nextCentral = Position.getPosition(nextPosition.getX() + height / 2, nextPosition.getY() + width / 2);
                Thing thing = world.findThing(world.getTileByLocation(nextCentral));
                if (thing != null && thing != this) {
                    ArrayList<Location> t = new ArrayList<>();
                    t.add(world.getTileByLocation(nextCentral));
                    world.handleAttack(new Attack(Attack.HIT, t, this.getAttack(), group));
                    //effect
                    Swoon swoon = new Swoon(this.p);
                    world.addItem(swoon);
                    //hit effect
                    Hit hit = new Hit();
                    hit.setPosition(this.p);
                    world.addItem(hit);
                    //if creature,hit away some distance
                    if (thing instanceof Creature) {
                        synchronized (thing) {
                            double d = Direction.calDirection(getCentralPosition(), thing.getCentralPosition());
                            double next_x = thing.getCentralPosition().getX() - Math.sin(d) * World.tileSize / 2;
                            double next_y = thing.getCentralPosition().getY() + Math.cos(d) * World.tileSize / 2;
                            world.ThingMove(thing, Position.getPosition((int) next_x, (int) next_y));
                        }
                    }
                } else if (world.positionOutOfBound(nextCentral)) {
                } else {
                    this.world.ThingMove(this, nextCentral);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
