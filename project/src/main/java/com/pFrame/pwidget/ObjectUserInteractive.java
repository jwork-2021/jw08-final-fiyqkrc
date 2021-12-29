package com.pFrame.pwidget;

import com.pFrame.Position;

import java.awt.event.*;

public interface ObjectUserInteractive {
    void mouseClicked(MouseEvent e, Position p);

    void keyPressed(KeyEvent e);

    void keyTyped(KeyEvent e);

    void keyReleased(KeyEvent e);

    void mouseEntered(MouseEvent arg0);

    void mouseExited(MouseEvent arg0);

    void mousePressed(MouseEvent arg0, Position p);

    void mouseReleased(MouseEvent arg0, Position p);

    void mouseWheelMoved(MouseWheelEvent e);

    Position getRealPosition();
}
