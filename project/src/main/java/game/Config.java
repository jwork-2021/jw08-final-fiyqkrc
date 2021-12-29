package game;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.HashMap;

public class Config {
    public static int WindowWidth;
    public static int WindowHeight;
    public static String DataPath;
    public static String LearningDataPath;

    public static int SceneWidth;
    public static int SceneHeight;
    public static int worldScale;
    public static int tileSize;

    public static double monsterOnGolang;
    public static double monsterOnRoom;

    public static int LogOutPutLevel;
    public static boolean LogTerminalOutput;
    public static String LogPath;

    private static JSONObject creatureInfos;
    public static HashMap<String, String[]> gameEnvInfos = new HashMap<>();
    public static String ExitNodeSource;

    public static String pathConvert(String path) {
        if (path.startsWith("~")) {
            return System.getProperty("user.home") + path.substring(1);
        } else if (path.startsWith("$")) {
            return Config.DataPath + path.substring(1);
        }
        return path;
    }

    static public void loadConfig(String absPath) {
        try {
            InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(absPath);
            byte[] bytes = inputStream.readAllBytes();
            JSONObject object = (JSONObject) JSONObject.parse(bytes);

            //load main data path
            JSONObject pathSet = (JSONObject) object.get("DataPath");
            if (System.getProperty("os.name").startsWith("win"))
                DataPath = (String) pathSet.get("win");
            else if (System.getProperty("os.name").startsWith("linux")) {
                DataPath = (String) pathSet.get("linux");
            } else
                DataPath = (String) pathSet.get("default");
            DataPath = Config.pathConvert(DataPath);
            if (!new File(DataPath).exists())
                new File(DataPath).mkdir();

            //load learningDataPath
            LearningDataPath = Config.pathConvert((String) object.get("learningDataPath"));
            if (!new File(LearningDataPath).exists())
                new File(LearningDataPath).mkdir();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void main(String[] args) {
        loadConfig("config.json");
    }

}


