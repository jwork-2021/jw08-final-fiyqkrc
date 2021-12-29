package log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Log {

    public static final int Error = 1;
    public static final int Warning = 2;
    public static final int Info = 3;

    private static String path = null;

    private static boolean terminalOutPut = true;

    private static int logLevel = 3;

    private static FileOutputStream stream = null;

    public static void setLogLevel(int a) {
        Log.logLevel = a;
    }

    public static void setTerminalOutPut(boolean select) {
        Log.terminalOutPut = select;
    }

    public static void setOutPath(String path) {
        Log.path = path;
        try {
            Log.stream = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void ErrorLog(Object obj, String message) {
        String msg = String.format("LOG: <%s> report Error: %s", obj.getClass().getName(), message);
        logOutPut(msg, Error);
    }

    private static void logOutPut(String msg, int error) {
        synchronized (Log.class) {
            if (Log.logLevel >= error) {
                if (Log.terminalOutPut)
                    System.out.println(msg);
                if (Log.path != null) {
                    try {
                        Log.stream.write((msg + "\n").getBytes());
                        Log.stream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void WarningLog(Object obj, String message) {
        String msg = String.format("LOG: <%s> report Warning: %s", obj.getClass().getName(), message);
        logOutPut(msg, Warning);
    }

    public static void InfoLog(Object obj, String message) {

        String msg = String.format("LOG: <%s> report Info: %s", obj.getClass().getName(), message);
        logOutPut(msg, Info);
    }
}
