package game.screen;

import com.pFrame.PTimer;
import com.pFrame.PTimerTask;
import com.pFrame.Position;
import com.pFrame.pwidget.PLabel;
import com.pFrame.pwidget.PWidget;

import java.awt.*;



public class MessageLabel extends PLabel {

    public MessageLabel(PWidget parent, Position p) {
        super(parent, p);
        setText("",1, Color.ORANGE);
    }

    public void sendMessage(String string,int time){
        setText(string,1,Color.ORANGE);
        PTimer timer=new PTimer();
        StopTask stopFunc=new StopTask(this);
        timer.schedule(stopFunc,false,time);
        Thread thread=new Thread(timer);
        thread.start();
    }

    class StopTask implements PTimerTask{
        public MessageLabel messageLabel;

        public StopTask(MessageLabel messageLabel){
            this.messageLabel=messageLabel;
        }

        @Override
        public void doTask() {
            messageLabel.setText("",1,Color.ORANGE);
        }
    }
}


