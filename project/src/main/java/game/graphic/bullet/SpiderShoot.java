package game.graphic.bullet;


import com.pFrame.Pixel;
import com.pFrame.Position;
import game.graphic.creature.monster.Spider;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

public class SpiderShoot extends Bullet {
    public static Pixel[][] image;

    static {
        image = GraphicItemGenerator.generateItem("image/shoot/spider.png", World.tileSize / 4, World.tileSize / 4).getPixels();
    }

    public SpiderShoot(Spider spider, double angle) {
        super(spider,angle);
        Pixel[][] pixels = image;
        this.width = World.tileSize/4;
        this.height = World.tileSize/4;
        graphic = pixels;
        this.speed = 200;
        this.p= Position.getPosition(parent.getCentralPosition().getX()-height/2, parent.getCentralPosition().getY()-width/2);
    }
}
