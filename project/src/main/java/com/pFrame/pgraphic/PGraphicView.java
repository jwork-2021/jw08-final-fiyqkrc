package com.pFrame.pgraphic;

import com.pFrame.Pixel;
import com.pFrame.Position;
import com.pFrame.pwidget.PWidget;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class PGraphicView extends PWidget implements PView {

    protected Position viewPosition;
    protected PGraphicScene scene;
    protected PGraphicItem focus;

    @Override
    public void setViewPosition(Position p) {
        this.viewPosition = p;
    }



    @Override
    public Position getViewPosition() {
        return this.viewPosition;
    }

    public PGraphicView(PWidget parent, Position p, PGraphicScene world) {
        super(parent, p);
        this.scene = world;
        world.setParentView(this);
        this.viewPosition = Position.getPosition(0, 0);
        this.focus = null;
    }

    @Override
    public Pixel[][] displayOutput() {
        adjustViewPosition();
        if(scene!=null)
            return this.scene.displayOutput(this.getViewPosition(), this.getWidgetWidth(), this.getWidgetHeight());
        else
            return null;
    }

    @Override
    public PGraphicItem getFocus() {
        return focus;
    }

    @Override
    public void setFocus(PGraphicItem thing) {
        this.focus = thing;
        this.viewPosition = Position.getPosition(thing.getPosition().getX() - this.getWidgetHeight() / 2,
                thing.getPosition().getY() - this.getWidgetWidth() / 2);
    }

    protected void adjustViewPosition() {
        if (this.getFocus() == null) {
            //Log.WarningLog(this, "focus thing not be set,so not exec"+
            //"adjustViewPosition");
        } else {
            Position focusPosition = this.getFocus().getPosition();
            Position viewPosition = this.getViewPosition();
            int absX = focusPosition.getX() - viewPosition.getX();
            int absY = focusPosition.getY() - viewPosition.getY();
            int resX;
            int resY;
            if (absX < this.getWidgetHeight() / 5) {
                resX = viewPosition.getX() - (this.getWidgetHeight()/5-absX);
            } else if (absX > this.getWidgetHeight() * 4 / 5) {
                resX = viewPosition.getX() + (absX-this.getWidgetHeight()*4/5);
            } else {
                resX = viewPosition.getX();
            }

            if (absY < this.getWidgetWidth() / 5) {
                resY = viewPosition.getY() - (this.getWidgetWidth()/5-absY);
            } else if (absY > this.getWidgetWidth() * 4 / 5) {
                resY = viewPosition.getY() + (absY-this.getWidgetWidth()*4/5);
            } else {
                resY = viewPosition.getY();
            }
            this.viewPosition = Position.getPosition(resX, resY);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
    }

    @Override
    public void mouseClicked(MouseEvent e, Position p) {
        super.mouseClicked(e, p);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        super.mouseEntered(arg0);
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        super.mouseExited(arg0);
    }

    @Override
    public void mousePressed(MouseEvent arg0, Position p) {
        super.mousePressed(arg0, p);
    }

    @Override
    public void mouseReleased(MouseEvent arg0, Position p) {
        super.mouseReleased(arg0, p);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
    }

}
