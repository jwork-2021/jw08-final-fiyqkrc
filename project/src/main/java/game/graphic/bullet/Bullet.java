package game.graphic.bullet;

import com.pFrame.Position;
import game.Attack;
import game.Location;
import game.graphic.Direction;
import game.graphic.Thing;
import game.graphic.creature.Creature;
import game.graphic.creature.monster.Monster;
import game.graphic.creature.monster.Vine;
import game.graphic.effect.BulletHit;
import game.graphic.interactive.GameThread;
import game.world.World;

import java.util.ArrayList;

public class Bullet extends Thing implements Runnable {
    protected final Creature parent;
    protected int group;
    protected int speed;
    protected double direction;

    protected double last_x;
    protected double last_y;
    protected long lastFlashPosition;
    protected Thread thread;

    public Bullet(Creature parent, double angle) {
        super(null);
        this.parent = parent;
        direction = angle;
        beCoverAble = true;
        this.group = parent.getGroup();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (this.world != null) {
                    long currentTime = System.currentTimeMillis();
                    double y = Math.sin(direction) * this.speed * (double) (currentTime - lastFlashPosition) / 1000 + last_y;
                    double x = Math.cos(direction) * this.speed * (double) (currentTime - lastFlashPosition) / 1000 + last_x;
                    lastFlashPosition = currentTime;

                    last_x = x - (int) x;
                    last_y = y - (int) y;

                    Position nextPosition = Position.getPosition(p.getX() - (int) y, p.getY() + (int) x);
                    Position nextCentral = Position.getPosition(nextPosition.getX() + height / 2, nextPosition.getY() + width / 2);
                    Thing thing = world.findThing(world.getTileByLocation(nextCentral));
                    if (thing instanceof Creature && ((Creature) thing).getGroup() != this.parent.getGroup()) {
                        ArrayList<Location> t = new ArrayList<>();
                        t.add(world.getTileByLocation(nextCentral));
                        world.handleAttack(new Attack(Attack.HIT, t, parent.getAttack(), group));
                        world.removeItem(this);

                        BulletHit bulletHit=new BulletHit();
                        bulletHit.setPosition(getCentralPosition());
                        world.addItem(bulletHit);
                        synchronized (thing) {
                            double d = Direction.calDirection(getCentralPosition(), thing.getCentralPosition());
                            double next_x = thing.getCentralPosition().getX() - Math.sin(d) * World.tileSize / 5;
                            double next_y = thing.getCentralPosition().getY() + Math.cos(d) * World.tileSize / 5;
                            world.ThingMove(thing, Position.getPosition((int) next_x, (int) next_y));
                        }

                        break;
                    } else if (thing != null && !(thing instanceof Creature)) {
                        if (thing instanceof Vine) {
                            if (parent instanceof Monster) {
                                this.world.ThingMove(this, nextCentral);
                                Thread.sleep(20);
                            } else {
                                world.removeItem(this);

                                BulletHit bulletHit=new BulletHit();
                                bulletHit.setPosition(getCentralPosition());
                                world.addItem(bulletHit);

                                break;
                            }
                        } else {
                            world.removeItem(this);

                            BulletHit bulletHit=new BulletHit();
                            bulletHit.setPosition(getCentralPosition());
                            world.addItem(bulletHit);

                            break;
                        }
                    } else if (world.positionOutOfBound(nextCentral)) {
                        world.removeItem(this);
                        break;
                    } else {
                        this.world.ThingMove(this, nextCentral);
                        Thread.sleep(20);
                    }
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GameThread.threadSet.remove(Thread.currentThread());
    }

    @Override
    public void whenBeAddedToScene() {
        super.whenBeAddedToScene();
        thread = new Thread(this);
        this.lastFlashPosition = System.currentTimeMillis();
        GameThread.threadSet.add(thread);
        thread.start();
    }
}

