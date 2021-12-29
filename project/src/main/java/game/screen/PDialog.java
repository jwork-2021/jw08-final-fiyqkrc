package game.screen;

import com.pFrame.Position;
import com.pFrame.pwidget.*;

import javax.swing.*;
import java.awt.*;

public class PDialog {
    PLabel pLabel;
    PButton pButton1;
    PButton pButton2;
    PLayout pLayout;
    PHeadWidget pHeadWidget;
    int result = 0;

    public static int Dialog(String message, String option1, String option2) {
        PDialog pDialog = new PDialog(message, option1, option2);

        while (pDialog.result == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return pDialog.result;
    }

    protected PDialog(String message, String option1, String option2) {
        pHeadWidget = new PHeadWidget(null, null, 200, 150);
        pHeadWidget.getLayout().setRCNumStyle(2, 1, "3x,1x", "1x");
        pLayout = new PLayout(pHeadWidget, Position.getPosition(2, 1), 1, 2);
        pButton1 = new PButton(pLayout, null);
        pButton2 = new PButton(pLayout, null);
        pLabel = new PLabel(pHeadWidget, Position.getPosition(1, 1));
        pLabel.setText(message, 1, Color.WHITE);

        pLabel.addBackground(PImage.getPureImage(Color.GRAY));
        pButton1.addBackground(PImage.getPureImage(Color.BLUE));
        pButton2.addBackground(PImage.getPureImage(Color.GREEN));
        pButton1.setText(option1, 1, Color.WHITE);
        pButton2.setText(option2, 1, Color.WHITE);

        try {
            pButton2.setClickFunc(this, this.getClass().getMethod("Button2Selected"));
            pButton1.setClickFunc(this, this.getClass().getMethod("Button1Selected"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        pHeadWidget.getPFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        pHeadWidget.startRepaintThread();
    }

    public void Button1Selected() {
        result = 1;
        pHeadWidget.dispose();
    }

    public void Button2Selected() {
        result = 2;
        pHeadWidget.dispose();
    }
}
