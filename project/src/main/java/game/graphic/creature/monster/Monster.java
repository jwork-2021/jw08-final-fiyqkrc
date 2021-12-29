package game.graphic.creature.monster;

import com.pFrame.Pixel;
import game.Location;
import game.controller.CreatureController;
import game.graphic.Direction;
import game.graphic.creature.Creature;
import game.graphic.effect.Dialog;

import java.awt.*;

abstract public class Monster extends Creature {
    private CreatureController oldController;
    protected Creature aim;

    public Monster(String path, int width, int height) {
        super(path, width, height);
        group = 1;
        speed = 2;
    }



    @Override
    public Creature searchAim() {
        try {
            if (aim == null) {
                Location location = world.searchNearestEnemy(this, 7);
                if (location != null && world.findThing(location) instanceof Creature) {
                    aim = (Creature) world.findThing(location);
                    Dialog dialog = new Dialog("Ya!!!", this.getPosition());
                    world.addItem(dialog);
                    return aim;
                } else
                    return null;
            } else {
                if (Math.abs(aim.getLocation().y() - this.getLocation().y()) < 7 && Math.abs(aim.getLocation().x() - getLocation().x()) < 7) {
                    return aim;
                } else {
                    aim = null;
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            aim = null;
            return aim;
        }
    }

    @Override
    public void whenBeAddedToScene() {
        super.whenBeAddedToScene();
    }

    public void tryMoveToEnemy(){
        if(aim!=null){
            try{
                double direction= Direction.calDirection(getCentralPosition(),aim.getCentralPosition());
                if(!move(direction)){
                    move(direction-Math.PI/2);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /*
    重写scene获取Item图像的方法，为怪物加上血量条
    */
    @Override
    public Pixel[][] getPixels() {
        Pixel[][] pixels = super.getPixels();
        int length = (int) (health * width / healthLimit);
        if (height > 1) {
            for (int b = 0; b < width; b++) {
                if (b < length) {
                    pixels[0][b] = Pixel.getPixel(Color.RED, (char) 0xf0);
                } else
                    pixels[0][b] = null;
            }
        }
        return pixels;
    }


}
