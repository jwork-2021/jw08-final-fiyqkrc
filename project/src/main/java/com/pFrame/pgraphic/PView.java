package com.pFrame.pgraphic;

import com.pFrame.Position;

public interface PView {

    void setViewPosition(Position p);

    Position getViewPosition();

    PGraphicItem getFocus();

    void setFocus(PGraphicItem thing);

}
