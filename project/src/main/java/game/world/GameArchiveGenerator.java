package game.world;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pFrame.Position;
import com.pFrame.pgraphic.PGraphicItem;
import game.graphic.creature.monster.*;
import game.graphic.env.CorridorFloor;
import game.graphic.env.Door;
import game.graphic.env.RoomFloor;
import game.graphic.env.Wall;
import game.graphic.interactive.Box;
import game.graphic.interactive.ExitPlace;
import worldGenerate.WorldGenerate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import static game.world.World.tileSize;

public class GameArchiveGenerator {
    private WorldGenerate worldGenerator;
    private int[][] worldArray;
    protected static ArrayList<WorldGenerate.Room> rooms;
    private Position startPosition;
    protected ArrayList<Class> monster = new ArrayList<>();


    private final JSONObject worldData;
    private final JSONArray itemsData;


    int width;
    int height;
    String path;
    int worldScale;
    int tileHeight;
    int tileWidth;

    public GameArchiveGenerator(int width, int height, String path, int scale) {
        this.width = width;
        this.height = height;
        this.path = path;
        this.worldScale = scale;
        worldData = new JSONObject();
        itemsData = new JSONArray();
        tileHeight = height / tileSize;
        tileWidth = width / tileSize;

        monster.add(Dragon.class);
        monster.add(Master.class);
        monster.add(Pangolin.class);
        monster.add(SnowMonster.class);
        monster.add(Spider.class);
    }

    public static void createPath(String path) {
        if (!new File(path).exists()) {
            if (!new File(new File(path).getParent()).exists()) {
                createPath(new File(path).getParent());
            }
            new File(path).mkdir();
        }
    }

    public static void createFile(String path) throws IOException {
        if (!new File(path).exists()) {
            if (!new File(new File(path).getParent()).exists()) {
                createPath(new File(path).getParent());
            }
            new File(path).createNewFile();
        }
    }

    public void generateWorldData() {
        mapInit();
        worldData.put("width", width);
        worldData.put("height", height);
        worldData.put("path", path);
        worldData.put("itemsData", itemsData);
        worldData.put("worldArray", worldArray);
        worldData.put("idCount", PGraphicItem.getIdCount());
        if (path != null) {
            try {
                createFile(path);
                FileOutputStream stream = new FileOutputStream(path);
                stream.write(worldData.toJSONString().getBytes());
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject getWorldData() {
        return worldData;
    }

    public void mapInit() {
        generateWorld();
        if (worldScale >= 2) {
            scaleWorld();
        }
        createWorld();
        createBox();
        createMonster();
    }

    public int[][] scaleWorld() {
        if (worldArray != null && worldScale >= 2) {
            int width = worldArray[0].length * worldScale;
            int height = worldArray.length * worldScale;
            int[][] array = new int[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    array[i][j] = worldArray[i / worldScale][j / worldScale];
                }
            }
            worldArray = array;
        }


        rooms = worldGenerator.getRoomsArray();
        for (WorldGenerate.Room room : rooms) {
            room.pos = Position.getPosition(room.pos.getX() * worldScale, room.pos.getY() * worldScale);
            room.height = room.height * worldScale;
            room.width = room.width * worldScale;
        }

        return worldArray;
    }

    protected void createBox() {
        Random random = new Random();
        for (WorldGenerate.Room room : rooms) {
            //generate box
            int x = random.nextInt(room.height);
            int y = random.nextInt(room.width);
            Box box = new Box();
            worldArray[x][y] = 200;
            box.setPosition(Position.getPosition((room.pos.getX() + x) * tileSize, (room.pos.getY() + y) * tileSize));
            itemsData.add(box.saveState());
        }
    }

    protected void createMonster() {
        Random random = new Random();

        //generate for rooms
        for (WorldGenerate.Room room : rooms) {
            for (int i = 0; i < room.width; i++)
                for (int j = 0; j < room.height; j++) {
                    if (random.nextDouble(1) > 0.9) {
                        int index = random.nextInt(monster.size());
                        worldArray[room.pos.getX() + j][room.pos.getY() + i] = 100 + index;
                        try {
                            Monster m = (Monster) monster.get(index).getDeclaredConstructor().newInstance();
                            m.setPosition(Position.getPosition((room.pos.getX() + j) * tileSize, (room.pos.getY() + i) * tileSize));
                            itemsData.add(m.saveState());
                        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }

        //generate for corridor
        for (int i = 0; i < worldArray.length; i++) {
            for (int j = 0; j < worldArray[0].length; j++) {
                if (worldArray[i][j] == 1) {
                    if (random.nextDouble(1) > 0.95) {
                        int index = random.nextInt(monster.size());
                        worldArray[i][j] = 100 + index;
                        try {
                            Monster m = (Monster) monster.get(index).getDeclaredConstructor().newInstance();
                            m.setPosition(Position.getPosition((i) * tileSize, (j) * tileSize));
                            itemsData.add(m.saveState());
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void generateWorld() {
        boolean success = false;
        int tryTimes = 0;
        while (tryTimes <= 3 && !success) {
            try {
                worldGenerator = new WorldGenerate(this.width / (tileSize * worldScale), this.height / (tileSize * worldScale), 2000000,
                        20, 2,
                        20, 2
                );
                worldArray = worldGenerator.generate();
                startPosition = Position.getPosition(worldGenerator.getStart().getX() * tileSize * worldScale, worldGenerator.getStart().getY() * tileSize * worldScale);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                tryTimes++;
            }
        }
    }

    private void createWorld() {
        for (int i = 0; i < tileHeight; i++) {
            for (int j = 0; j < tileWidth; j++) {
                switch (worldArray[i][j]) {
                    case 0 -> {
                        Wall wall = new Wall();
                        wall.setPosition(Position.getPosition(i * tileSize, j * tileSize));
                        itemsData.add(wall.saveState());
                    }
                    case 1 -> {
                        CorridorFloor corridorFloor = new CorridorFloor();
                        corridorFloor.setPosition(Position.getPosition(i * tileSize, j * tileSize));
                        itemsData.add(corridorFloor.saveState());
                    }
                    case 5 -> {
                        Door door = new Door();
                        door.setPosition(Position.getPosition(i * tileSize, j * tileSize));
                        itemsData.add(door.saveState());

                        ExitPlace exitPlace = new ExitPlace();
                        exitPlace.setPosition(Position.getPosition(i * tileSize, j * tileSize));
                        itemsData.add(exitPlace.saveState());
                    }
                    case 4 -> {
                        Door door = new Door();
                        door.setPosition(Position.getPosition(i * tileSize, j * tileSize));
                        itemsData.add(door.saveState());
                    }
                    case 2, 3, 6 -> {
                        RoomFloor roomFloor = new RoomFloor();
                        roomFloor.setPosition(Position.getPosition(i * tileSize, j * tileSize));
                        itemsData.add(roomFloor.saveState());
                    }
                    default -> {
                    }
                }
            }
        }
    }


}
