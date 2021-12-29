package game.graphic.creature;

import com.pFrame.Position;
import game.controller.CreatureController;
import game.world.World;

public interface Controllable {
    void setController(CreatureController controller);

    CreatureController getController();

    void responseToEnemy();

    boolean move(double direction);

    int getId();

    void speak(String text);

    Position getCentralPosition();

    void dead();

    boolean isDead();

    Creature searchAim();

    int getColdTime();

    double getAttackRange();

    World getWorld();
}
