package com.pFrame.pwidget;

import com.pFrame.Position;

public class PHeadWidget extends PWidget {

    PFrame pFrame;

    public PHeadWidget(PWidget parent, Position p, int width, int height) {
        super(parent, p);
        this.pFrame = new PFrame(width, height);
        this.changeWidgetSize(pFrame.getFrameWidth(), pFrame.getFrameHeight());
        this.pFrame.setHeadWidget(this);
        this.pFrame.setVisible(true);
        this.layout = new PLayout(this, null, 1, 1);
        this.layout.changeWidgetSize(this.widgetWidth, this.widgetHeight);
        this.layout.setPosition(Position.getPosition(0, 0));
    }

    public PHeadWidget(PWidget parent, Position p) {
        this(parent, p, 500, 400);
    }

    public void dispose() {
        if (repaintThread != null)
            repaintThread.interrupt();
        pFrame.dispose();
    }

    public PFrame getPFrame() {
        return pFrame;
    }

    Thread repaintThread;

    public void startRepaintThread() {
        repaintThread = new Thread(pFrame, "window flash thread");
        repaintThread.start();
    }

    public void addPFrameKeyListener(int ch, PFrameKeyListener pFrameKeyListener) {
        pFrame.addPFrameKeyListener(ch, pFrameKeyListener);
    }

    public void freePFrameKeyListener(int ch, PFrameKeyListener pFrameKeyListener) {
        pFrame.freePFrameKeyListener(ch, pFrameKeyListener);
    }
}