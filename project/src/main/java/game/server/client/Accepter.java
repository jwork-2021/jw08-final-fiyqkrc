package game.server.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import game.graphic.creature.Controllable;

import java.util.ArrayDeque;

public class Accepter {
    final ArrayDeque<JSONObject> queue;

    public Accepter() {
        queue = new ArrayDeque<>();
    }

    public static JSONObject MoveMessage(Controllable controllable, double direction) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "move");
        jsonObject.put("id", controllable.getId());
        jsonObject.put("direction", direction);
        return jsonObject;
    }

    public static JSONObject attackMessage(Controllable controllable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "attack");
        jsonObject.put("id", controllable.getId());
        return jsonObject;
    }

    public static JSONObject deadMessage(Controllable controllable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "dead");
        jsonObject.put("id", controllable.getId());
        return jsonObject;
    }


    public void submit(JSONObject message) {
        synchronized (queue) {
            message.put("timestamp", System.currentTimeMillis());
            queue.push(message);
        }
    }

    public JSONArray getMessage() {
        synchronized (queue) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(queue);
            queue.clear();
            return jsonArray;
        }
    }
}
