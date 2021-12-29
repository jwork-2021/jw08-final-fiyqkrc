package game.server.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import game.screen.UI;
import game.server.Message;
import game.world.World;
import log.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMain implements Runnable {
    Accepter commandListener = new Accepter();
    static ClientMain instance;
    Socket socket;
    public UI ui;

    private ClientMain() {
    }

    public void connect(String host, int port) throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        Log.InfoLog(this, "client connect to " + host + ":" + port);
    }

    private final ExecutorService messageSendingEs = Executors.newSingleThreadExecutor();

    public void sendMessage(String string) {
        messageSendingEs.submit(() -> {
            try {
                synchronized (socket.getOutputStream()) {
                    socket.getOutputStream().write(string.getBytes());
                    socket.getOutputStream().flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    World world;

    public void setWorld(World world) {
        this.world = world;
    }

    public static ClientMain getInstance() {
        if (instance == null) {
            instance = new ClientMain();
        }
        return instance;
    }

    public Accepter getCommandListener() {
        return commandListener;
    }

    private int frameCount = 0;

    private void analysis(JSONObject jsonObject) {
        if (Objects.equals(jsonObject.getObject(Message.messageClass, String.class), Message.FrameSync)) {
            if (frameCount == 0) {
                frameCount = jsonObject.getObject(Message.moreArgs, Integer.class);
            } else {
                int frame = jsonObject.getObject(Message.moreArgs, Integer.class);
                if (frame - frameCount == 1) {
                    frameCount++;
                    if (world != null)
                        world.frameSync(jsonObject.getObject(Message.information, JSONArray.class));
                } else {
                    Log.WarningLog(this, "frame lost you try state sync...");
                    frameCount = 0;
                    sendMessage(Message.getNeedStateSyncMessage());
                }
            }
        } else if (Objects.equals(jsonObject.getObject(Message.messageClass, String.class), Message.GameInit)) {
            World world = new World(jsonObject.getObject(Message.information, JSONObject.class));
            setWorld(world);
            ui.setWorld(world);
            world.screen = ui;
            world.activeControlRole();
        } else if (Objects.equals(jsonObject.getObject(Message.messageClass, String.class), Message.StateSync)) {
            String message = Message.getLaunchStateSyncMessage(world.getCurrentState());
            sendMessage(message);
        } else if (Objects.equals(jsonObject.getObject(Message.messageClass, String.class), Message.StartStateSync)) {
            world.stateSync(jsonObject.getObject(Message.information, JSONArray.class));
        } else if (Objects.equals(jsonObject.getObject(Message.messageClass, String.class), Message.PlayerJoin)) {
            world.addMultiPlayer(jsonObject.getObject(Message.information, JSONObject.class));
        } else if (Objects.equals(jsonObject.getObject(Message.messageClass, String.class), Message.PlayerQuit)) {
            world.removeMultiPlayer(jsonObject.getObject(Message.information, Integer.class));
        } else {
            System.out.println(jsonObject.toJSONString());
        }
    }

    @Override
    public void run() {
        if (socket != null) {
            Log.InfoLog(this, "client start working...");
            Thread inputListener = new Thread(() -> {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()),
                            10240000);
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            String jsonStr = null;
                            try {
                                jsonStr = bufferedReader.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                                ui.gameExit();
                            }
                            if (jsonStr == null) {
                                socket.close();
                                Log.ErrorLog(this, "socket may closed");
                                ui.gameExit();
                            } else {
                                JSONObject jsonObject = JSON.parseObject(jsonStr);
                                analysis(jsonObject);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputListener.start();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    new Thread(() -> {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(Message.messageClass, Message.FrameSync);
                        jsonObject.put(Message.information, commandListener.getMessage());
                        sendMessage(Message.JSON2MessageStr(jsonObject));
                    }).start();
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    inputListener.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            Log.InfoLog(this, "client stop working");
        } else {
            Log.ErrorLog(this, "you have not connect to any server ");
        }
    }
}
