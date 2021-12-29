package com.pFrame;

import game.graphic.interactive.GameThread;
import log.Log;

public class PTimer implements Runnable {
    private int time;
    private boolean repeat;
    private PTimerTask tasker;
    private boolean stop;

    public void schedule(PTimerTask task, boolean repeat, int time) {
        this.tasker = task;
        this.repeat = repeat;
        this.time = time;
    }

    public PTimer() {
        this.time = 0;
        this.repeat = false;
        this.tasker = null;
        this.stop = false;
    }

    @Override
    public void run() {
        if (this.tasker == null || this.time <= 0) {
            Log.ErrorLog(this, String.format("Invalid args: %s %d", tasker, this.time));
        } else {
            if (repeat) {
                while (!this.stop && !Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(time);
                        this.tasker.doTask();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } else {
                try {
                    Thread.sleep(this.time);
                    this.tasker.doTask();
                } catch (InterruptedException ignored) {
                }
            }
        }
        GameThread.threadSet.remove(Thread.currentThread());
    }

    public void stop() {
        this.stop = true;
    }
}
