package game.graphic.bullet;

import com.pFrame.Pixel;
import com.pFrame.Position;
import game.graphic.creature.monster.Dragon;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

public class Wind extends Bullet {
    public static Pixel[][] image;

    static {
        image = GraphicItemGenerator.generateItem("image/shoot/wind.png", World.tileSize, World.tileSize).getPixels();
    }

    public Wind(Dragon dragon, double angle) {
        super(dragon, angle);
        Pixel[][] pixels = image;
        this.width = World.tileSize;
        this.height = World.tileSize;
        graphic = pixels;
        speed = 60;
        this.p = Position.getPosition(parent.getCentralPosition().getX() - height / 2, parent.getCentralPosition().getY() - width / 2);
    }
}
