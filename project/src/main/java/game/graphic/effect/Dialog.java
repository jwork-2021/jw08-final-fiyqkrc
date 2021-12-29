package game.graphic.effect;

import com.pFrame.PFont;
import com.pFrame.PTimerTask;
import com.pFrame.Pixel;
import com.pFrame.Position;
import imageTransFormer.GraphicItemGenerator;

import java.util.Objects;

public class Dialog extends Effect {
    String string ;

    public Dialog(String text, Position position) {
        this(text,position,2000);
    }

    public Dialog(String text, Position position,int time) {
        super();
        this.string=text;
        Pixel[][] pixels=Pixel.valueOf(Objects.requireNonNull(GraphicItemGenerator.generateItem("image/dialog.png", PFont.fontBaseSize * string.length() + 10, 12)));
        for(int i=0;i<string.length();i++)
            Pixel.pixelsAdd(pixels, PFont.getCharByPixels(string.charAt(i)),Position.getPosition(2,2+PFont.fontBaseSize*i));
        this.graphic=pixels;
        this.width=PFont.fontBaseSize*string.length()+4;
        this.height=12;
        repeat=false;
        this.time=time;
        task=new Task(this);
        this.p=Position.getPosition(position.getX()-4,position.getY()-PFont.fontBaseSize*string.length()-4);
    }

    class Task implements PTimerTask{
        Dialog dialog;

        public Task(Dialog dialog){
            this.dialog=dialog;
        }

        @Override
        public void doTask() {
            dialog.world.removeItem(dialog);
        }
    }
}
