package com.pFrame.pwidget;

import com.pFrame.Pixel;
import com.pFrame.Position;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PWidget implements ObjectUserInteractive {
    protected int widgetHeight;
    protected int widgetWidth;
    protected Position position;
    protected PLayout layout;
    protected PWidget parent;
    protected PWidget background;
    protected Pixel[][] pixels ;


    protected ArrayList<PWidget> childWidgets;

    public void addBackground(PWidget background) {

        this.background = background;
        if(background!=null) {
            this.background.setPosition(Position.getPosition(0, 0));
            this.background.changeWidgetSize(this.getWidgetWidth(), this.getWidgetHeight());
            this.background.setParent(this);
        }
    }

    public PWidget getBackground() {
        return this.background;
    }

    public PWidget(PWidget parent, Position p) {
        this.parent = parent;
        this.childWidgets=new ArrayList<>();
        if (this.parent != null)
            this.parent.addChildWidget(this, p);
        else {
            this.widgetHeight = 0;
            this.widgetWidth = 0;
            this.position = Position.getPosition(0, 0);
        }
        pixels= Pixel.emptyPixels(this.widgetWidth, this.widgetHeight);
    }

    public void addChildWidget(PWidget widget, Position p) {
        if (this.layout != null)
            this.layout.addChildWidget(widget,p);
            //this.layout.autoSetPosition(widget, p);
        else {
            widget.setParent(this);
            if(p==null)
                widget.setPosition(p);
            else{
                widget.setPosition(Position.getPosition(0,0));
            }
            this.childWidgets.add(widget);
            //widget.changeWidgetSize(this.getWidgetWidth(), this.getWidgetHeight());
        }
    }

    public void resetLayout(PLayout layout){
        for(PWidget widget:childWidgets){
            if(widget==this.layout) {
                childWidgets.remove(widget);
                childWidgets.add(layout);
            }
        }
        this.layout=layout;
        if(layout!=null) {
            this.layout.setParent(this);
            this.layout.setPosition(Position.getPosition(0, 0));
            this.layout.changeWidgetSize(this.widgetWidth, this.widgetHeight);
        }
    }

    protected void setLayout(PLayout layout) {
        this.layout = layout;
    }

    public PLayout getLayout() {
        return this.layout;
    }

    public void setParent(PWidget widget) {
        this.parent = widget;
    }

    public PWidget getParentWidget() {
        return this.parent;
    }

    public Pixel[][] displayOutput() {
        synchronized (this) {
            if (this.getWidgetHeight() <= 0 || this.getWidgetWidth() <= 0) {
                return null;
            } else {
                Pixel[][] frame=Pixel.emptyPixels(widgetWidth,widgetHeight);
                if (this.background != null){
                    frame = Pixel.pixelsAdd(frame, this.background.displayOutput(), this.background.getPosition());
                }
                for (PWidget widget : this.childWidgets) {
                    Pixel.pixelsAdd(frame, widget.displayOutput(), widget.getPosition());
                }
                pixels=frame;
                return pixels;
            }
        }
    }

    public synchronized void removeWidget(PWidget widget){
        if(widget==layout){
            this.layout=null;
            this.childWidgets.remove(widget);
        }
        else if(this.childWidgets.contains(widget)){
            childWidgets.remove(widget);
        }
        else{
            this.layout.removeWidget(widget);
        }
    }

    public int getWidgetHeight() {
        return this.widgetHeight;

    }

    public int getWidgetWidth() {
        return this.widgetWidth;
    }

    public void changeWidgetSize(int width, int height) {
        synchronized (this) {
            this.widgetHeight = height;
            this.widgetWidth = width;
            this.sizeChanged();
        }
    }

    protected void sizeChanged() {
        if(this.background!=null){
            this.background.changeWidgetSize(this.widgetWidth,this.widgetHeight);
        }
        if(this.layout!=null){
            this.layout.changeWidgetSize(this.widgetWidth,this.widgetHeight);
        }
        pixels = Pixel.emptyPixels(this.widgetWidth, this.widgetHeight);
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public ArrayList<PWidget> getChildWidget() {
        ArrayList<PWidget> res = new ArrayList<PWidget>();
        res.add(this);
        if (this.layout != null)
            res.addAll(this.layout.getChildWidget());
        res.addAll(childWidgets);
        return res;
    }

    public ArrayList<PWidget> getWidgetsAt(Position p) {
        ArrayList<PWidget> res = new ArrayList<PWidget>();
        res.add(this);
        if (this.layout != null)
            res.addAll(this.layout.getWidgetsAt(p));
        for(PWidget widget:this.childWidgets) {
            if (widget != layout)
                if (WidgetRange.inRange(widget.position, widget.widgetWidth, widget.widgetHeight, p)) {
                    res.add(widget);
                }
        }
        return res;
    }

    public static class WidgetRange {
        public static boolean inRange(Position area, int width, int height, Position src) {
            if (src.getX() >= area.getX() && src.getX() < area.getX() + height)
                return src.getY() >= area.getY() && src.getY() < area.getY() + width;
            else
                return false;
        }
    }

    public Position getRealPosition() {
        if (this.parent != null)
            return Position.getPosition(this.getPosition().getX() + this.getParentWidget().getRealPosition().getX(),
                    this.getPosition().getY() + this.getParentWidget().getRealPosition().getY());
        else {
            return this.getPosition();
        }
    }

    protected Method clickMethod;
    protected Object clickMethodObject;

    public void setClickFunc(Object object,Method method){
        clickMethod=method;
        clickMethodObject=object;
    }

    public void mouseClicked(MouseEvent mouseEvent, Position p) {

        if(clickMethod!=null&&clickMethodObject!=null) {
            try {
                clickMethod.invoke(clickMethodObject, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void mouseEntered(MouseEvent arg0) {

    }

    public void mouseExited(MouseEvent arg0) {

    }

    public void mousePressed(MouseEvent arg0, Position p) {

    }

    public void mouseReleased(MouseEvent arg0, Position p) {

    }

    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    public void addPFrameKeyListener(int ch,PFrameKeyListener pFrameKeyListener){
        if(this.parent!=null){
            this.parent.addPFrameKeyListener(ch,pFrameKeyListener);
        }
    }

    public void freePFrameKeyListener(int ch,PFrameKeyListener pFrameKeyListener){
        if(this.parent!=null){
            this.parent.freePFrameKeyListener(ch,pFrameKeyListener);
        }
    }
}
