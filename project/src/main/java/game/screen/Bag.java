package game.screen;

import com.pFrame.Position;
import com.pFrame.pwidget.PLayout;
import com.pFrame.pwidget.PWidget;

public class Bag extends PLayout {
    public Bag(PWidget parent, Position p) {
        super(parent, p);
        setRCNumStyle(4,2,"1x,8,1x,8","1x,1x");
    }
}
