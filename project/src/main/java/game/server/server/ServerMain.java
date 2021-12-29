package game.server.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import game.Config;
import game.graphic.creature.operational.Calabash;
import game.server.Message;
import game.world.GameArchiveGenerator;
import log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    public static int maxClientNum = 4;
    ServerSocket serverSocket;
    int currentClient;
    Socket firstSocket;
    ExecutorService es;
    Thread server;
    CopyOnWriteArraySet<Socket> sockets;
    private final JSONArray messageArray = new JSONArray();

    public ServerMain() throws IOException {
        sockets = new CopyOnWriteArraySet<>();
        es = Executors.newFixedThreadPool(100);
        currentClient = 0;
        serverSocket = new ServerSocket(9000);
    }

    private void stateSync(JSONArray jsonArray) throws IOException {
        String message = Message.getStartStateSyncCommand(jsonArray);
        for (Socket socket : sockets) {
            try {
                if (socket != firstSocket) {
                    sendMessage(socket, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ExecutorService messageSendingEs = Executors.newFixedThreadPool(16);

    private void sendMessage(Socket socket, String string) {
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

    Thread frameSyncThread;
    Thread stateSyncThread;

    JSONObject worldData;

    public void start() {

        // generate world
        GameArchiveGenerator gameArchiveGenerator = new GameArchiveGenerator(2000, 2000,
                Config.DataPath + "/server/game.json", 2);
        gameArchiveGenerator.generateWorldData();

        worldData = gameArchiveGenerator.getWorldData();

        // create && start frame sync thread
        frameSyncThread = new Thread(() -> {

            int frameCount = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Message.messageClass, Message.FrameSync);
                    jsonObject.put(Message.moreArgs, frameCount);
                    String message;
                    synchronized (messageArray) {
                        jsonObject.put(Message.information, messageArray);
                        message = Message.JSON2MessageStr(jsonObject);
                        messageArray.clear();
                    }
                    for (Socket socket : sockets) {
                        sendMessage(socket, message);
                    }
                    frameCount++;
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // create && start state sync thread
        stateSyncThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (firstSocket != null) {
                    try {
                        sendMessage(firstSocket, Message.getStateSyncBroadcast());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        stateSyncThread.start();
        frameSyncThread.start();
        HashMap<Socket, Integer> SocketCalabashMap = new HashMap<>();

        // start server accept main thread
        server = new Thread(() -> {
            Log.InfoLog(this, "server listener start work...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (currentClient >= maxClientNum) {
                        sendMessage(clientSocket,
                                Message.JSON2MessageStr(Message.getErrorMessage(Message.OutOfMaxClientBound)));
                        clientSocket.close();
                    } else {
                        currentClient++;
                        sockets.add(clientSocket);
                        if (firstSocket == null)
                            firstSocket = clientSocket;

                        // submit a NIO handle thread to executors
                        es.submit(new Runnable() {
                            private final Socket socket = clientSocket;

                            private void closeConnection() {
                                try {
                                    sendMessage(socket, Message.JSON2MessageStr(Message.getGameQuitMessage()));
                                    socket.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    currentClient -= (sockets.remove(socket) ? 1 : 0);
                                    SocketCalabashMap.remove(socket);
                                    Thread.currentThread().interrupt();
                                }
                            }

                            private void handleMessage(String jsonStr) {
                                // System.out.println(jsonStr);
                                try {
                                    JSONObject jsonObject = JSON.parseObject(jsonStr);
                                    String messageClass = jsonObject.getObject(Message.messageClass, String.class);
                                    if (Objects.equals(messageClass, Message.ErrorMessage)) {
                                        Log.ErrorLog(this, jsonObject.getObject(Message.information, String.class));
                                    } else if (Objects.equals(messageClass, Message.FrameSync)) {
                                        synchronized (messageArray) {
                                            messageArray
                                                    .addAll(jsonObject.getObject(Message.information, JSONArray.class));
                                        }
                                    } else if (Objects.equals(messageClass, Message.LaunchStateSync)) {
                                        if (socket != firstSocket) {
                                            Log.ErrorLog(this, "a launchStateSync request from nonMainClient blocked");
                                        } else {
                                            stateSync(jsonObject.getObject(Message.information, JSONArray.class));
                                        }
                                    } else if (Objects.equals(messageClass, Message.GameQuit)) {
                                        Thread.currentThread().interrupt();
                                    } else if (Objects.equals(messageClass, Message.NeedStateSync)) {
                                        sendMessage(firstSocket, Message.getStateSyncBroadcast());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Thread.currentThread().interrupt();
                                }
                            }

                            @Override
                            public void run() {
                                try {
                                    // create calabash and send to client for init
                                    Calabash calabash = new Calabash();
                                    SocketCalabashMap.put(socket, calabash.getId());
                                    worldData.getObject("itemsData", JSONArray.class).add(calabash.saveState());
                                    worldData.remove("controlRole");
                                    worldData.put("controlRole", calabash.getId());
                                    sendMessage(socket, Message.getWorldInitCommand(worldData));

                                    if (socket != firstSocket) {
                                        // send add player command to mainClient
                                        sendMessage(firstSocket, Message.getAddPlayerCommand(calabash.saveState()));
                                    }

                                    // start input listener && handle thread
                                    BufferedReader bufferedReader = new BufferedReader(
                                            new InputStreamReader(socket.getInputStream()), 10240000);
                                    while (!Thread.currentThread().isInterrupted()) {
                                        handleMessage(bufferedReader.readLine());
                                    }
                                    System.out.println("client socket quit");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (firstSocket == socket) {
                                        stop();
                                    } else {
                                        try {
                                            sendMessage(firstSocket,
                                                    Message.getPlayerQuitCommand(SocketCalabashMap.get(socket)));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.ErrorLog(this, "deadly error ,quit now...");
                                            stop();
                                        }
                                    }
                                    closeConnection();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.InfoLog(this, "server listener quit...");
        });
        server.start();
    }

    public void stop() {
        try {
            serverSocket.close();
            server.interrupt();
            frameSyncThread.interrupt();
            stateSyncThread.interrupt();
            es.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.InfoLog(this, "server stop...");
    }
}
