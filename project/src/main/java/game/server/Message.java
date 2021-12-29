package game.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Message {
    public static String messageClass="messageClass";
    public static String FrameSync="FrameSync";
    public static String StateSync="StateSync";
    public static String LaunchStateSync="LaunchSync";
    public static String NeedStateSync="NeedSync";
    public static String StartStateSync="StartStateSync";
    public static String ErrorMessage="ErrorMessage";
    public static String GameQuit="GameQuit";
    public static String GameInit="GameInit";
    public static String PlayerJoin="PlayerJoin";
    public static String PlayerQuit="PlayerQuit";
    public static String OutOfMaxClientBound="OutOfMaxClientBound";

    public static String moreArgs="moreArgs";
    public static String SubmitInput="SubmitInput";

    public static String information="Information";

    public static JSONObject getErrorMessage(String str){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(messageClass,ErrorMessage);
        jsonObject.put(information,str);
        return jsonObject;
    }

    public static JSONObject getGameQuitMessage(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(messageClass,GameQuit);
        return jsonObject;
    }

    public static String getWorldInitCommand(JSONObject worldData){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(information,worldData);
        jsonObject.put(messageClass,GameInit);
        return JSON2MessageStr(jsonObject);
    }

    public static String getAddPlayerCommand(JSONObject object){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(information,object);
        jsonObject.put(messageClass,PlayerJoin);
        return JSON2MessageStr(jsonObject);
    }

    public static String getPlayerQuitCommand(int id){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(information,id);
        jsonObject.put(messageClass,PlayerQuit);
        return JSON2MessageStr(jsonObject);
    }

    public static String getStateSyncBroadcast(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(messageClass,StateSync);
        return JSON2MessageStr(jsonObject);
    }

    public static String getLaunchStateSyncMessage(JSONArray jsonArray){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(messageClass,LaunchStateSync);
        jsonObject.put(information,jsonArray);
        return JSON2MessageStr(jsonObject);
    }

    public static String getNeedStateSyncMessage(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(messageClass,NeedStateSync);
        return JSON2MessageStr(jsonObject);
    }

    public static String getStartStateSyncCommand(JSONArray jsonArray){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(messageClass,StartStateSync);
        jsonObject.put(information,jsonArray);
        return JSON2MessageStr(jsonObject);
    }

    public static String JSON2MessageStr(JSONObject jsonObject){
        return jsonObject.toJSONString()+"\n";
    }
}
