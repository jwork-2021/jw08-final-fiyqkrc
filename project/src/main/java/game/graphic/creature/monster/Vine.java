package game.graphic.creature.monster;

import com.pFrame.PTimer;
import com.pFrame.PTimerTask;
import com.pFrame.Pixel;
import com.pFrame.Position;
import game.Attack;
import game.Location;
import game.graphic.creature.Creature;
import game.graphic.effect.Effect;
import game.world.World;
import imageTransFormer.GraphicItemGenerator;

import java.util.ArrayList;
import java.util.Random;

public class Vine extends Effect {
    public static Pixel[][][] images;

    static {
        images = new Pixel[28][][];
        images[0] = GraphicItemGenerator.generateItem("image/shoot/vine/1.png", World.tileSize, World.tileSize).getPixels();
        images[1] = GraphicItemGenerator.generateItem("image/shoot/vine/2.png", World.tileSize, World.tileSize).getPixels();
        images[2] = GraphicItemGenerator.generateItem("image/shoot/vine/3.png", World.tileSize, World.tileSize).getPixels();
        images[3] = GraphicItemGenerator.generateItem("image/shoot/vine/4.png", World.tileSize, World.tileSize).getPixels();
        images[4] = GraphicItemGenerator.generateItem("image/shoot/vine/5.png", World.tileSize, World.tileSize).getPixels();
        images[5] = GraphicItemGenerator.generateItem("image/shoot/vine/6.png", World.tileSize, World.tileSize).getPixels();
        images[6] = GraphicItemGenerator.generateItem("image/shoot/vine/7.png", World.tileSize, World.tileSize).getPixels();
        images[7] = GraphicItemGenerator.generateItem("image/shoot/vine/8.png", World.tileSize, World.tileSize).getPixels();
        images[8] = GraphicItemGenerator.generateItem("image/shoot/vine/9.png", World.tileSize, World.tileSize).getPixels();
        images[9] = GraphicItemGenerator.generateItem("image/shoot/vine/10.png", World.tileSize, World.tileSize).getPixels();
        images[10] = GraphicItemGenerator.generateItem("image/shoot/vine/11.png", World.tileSize, World.tileSize).getPixels();
        images[11] = GraphicItemGenerator.generateItem("image/shoot/vine/12.png", World.tileSize, World.tileSize).getPixels();
        images[12] = GraphicItemGenerator.generateItem("image/shoot/vine/13.png", World.tileSize, World.tileSize).getPixels();
        images[13] = GraphicItemGenerator.generateItem("image/shoot/vine/14.png", World.tileSize, World.tileSize).getPixels();
        images[14] = GraphicItemGenerator.generateItem("image/shoot/vine/15.png", World.tileSize, World.tileSize).getPixels();
        images[15] = GraphicItemGenerator.generateItem("image/shoot/vine/16.png", World.tileSize, World.tileSize).getPixels();
        images[16] = GraphicItemGenerator.generateItem("image/shoot/vine/17.png", World.tileSize, World.tileSize).getPixels();
        images[17] = GraphicItemGenerator.generateItem("image/shoot/vine/18.png", World.tileSize, World.tileSize).getPixels();
        images[18] = GraphicItemGenerator.generateItem("image/shoot/vine/19.png", World.tileSize, World.tileSize).getPixels();
        images[19] = GraphicItemGenerator.generateItem("image/shoot/vine/20.png", World.tileSize, World.tileSize).getPixels();
        images[20] = GraphicItemGenerator.generateItem("image/shoot/vine/21.png", World.tileSize, World.tileSize).getPixels();
        images[21] = GraphicItemGenerator.generateItem("image/shoot/vine/22.png", World.tileSize, World.tileSize).getPixels();
        images[22] = GraphicItemGenerator.generateItem("image/shoot/vine/23.png", World.tileSize, World.tileSize).getPixels();
        images[23] = GraphicItemGenerator.generateItem("image/shoot/vine/24.png", World.tileSize, World.tileSize).getPixels();
        images[24] = GraphicItemGenerator.generateItem("image/shoot/vine/25.png", World.tileSize, World.tileSize).getPixels();
        images[25] = GraphicItemGenerator.generateItem("image/shoot/vine/26.png", World.tileSize, World.tileSize).getPixels();
        images[26] = GraphicItemGenerator.generateItem("image/shoot/vine/27.png", World.tileSize, World.tileSize).getPixels();
        images[27] = GraphicItemGenerator.generateItem("image/shoot/vine/28.png", World.tileSize, World.tileSize).getPixels();
    }

    public Vine(Creature creature, Position position) {
        super();
        this.width = World.tileSize;
        this.height = World.tileSize;
        this.graphic = images[new Random().nextInt(28)];
        time = 1500;
        repeat = true;
        task = new Vine.Task(creature, timer, position, this);
        this.p = position;
        this.beCoverAble = false;

    }

    class Task implements PTimerTask {
        int timers = 0;
        Creature creature;
        PTimer timer;
        Position position;
        Vine vine;


        public Task(Creature creature, PTimer timer, Position position, Vine vine) {
            this.creature = creature;
            this.timer = timer;
            this.position = position;
            this.vine = vine;
        }

        @Override
        public void doTask() {
            timers++;
            if (timers <= 3) {
                ArrayList<Location> affectList = new ArrayList<>();
                affectList.add(creature.getWorld().getTileByLocation(this.position));
                Attack attack = new Attack(Attack.HIT, affectList, creature.getAttack(), creature.getGroup());
                creature.getWorld().handleAttack(attack);
            } else {
                world.removeItem(vine);
                this.timer.stop();
            }
        }
    }
}
