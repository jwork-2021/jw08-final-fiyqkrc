package game.graphic.interactive;

import java.util.concurrent.CopyOnWriteArraySet;

public interface GameThread {
    //public static Set<Thread> threadSet=new HashSet<>();
    public static final CopyOnWriteArraySet<Thread> threadSet=new CopyOnWriteArraySet<>();

    public Thread getThread();
    public void stop();
}
