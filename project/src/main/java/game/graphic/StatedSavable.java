package game.graphic;

import com.alibaba.fastjson.JSONObject;

public interface StatedSavable {
    public JSONObject saveState();
    public void resumeState(JSONObject jsonObject);
}
