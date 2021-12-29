package game.graphic;

import com.alibaba.fastjson.JSONObject;
import com.pFrame.Pixel;
import com.pFrame.Position;
import com.pFrame.pgraphic.PGraphicItem;
import game.Location;
import game.world.Tile;
import game.world.World;

public class Thing extends PGraphicItem {
    protected Tile<? extends Thing> tile;
    protected boolean beCoverAble;
    protected World world;

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Position getCentralPosition() {
        if (this.width != 0 && this.height != 0) {
            return Position.getPosition(p.getX() + height / 2, p.getY() + width / 2);
        } else
            return p;
    }

    public Location getLocation() {
        if (this.getTile() != null) {
            return this.tile.getLocation();
        } else
            return new Location((p.getX() + height / 2) / World.tileSize, (p.getY() + width / 2) / World.tileSize);
    }

    public boolean isBeCoverAble() {
        return beCoverAble;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBeCoverAble(boolean coverAble) {
        beCoverAble = coverAble;
    }

    public Thing(String absPath, int width, int height) {
        super(absPath, width, height);
        beCoverAble = true;
    }

    public Thing(Pixel[][] pixels) {
        super(pixels);
        beCoverAble = true;
    }

    public void setTile(Tile<? extends Thing> tile) {
        this.tile = tile;
    }

    public Tile<? extends Thing> getTile() {
        return this.tile;
    }

    public void whenBeAddedToScene() {

    }

    public JSONObject save() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("position", p.toString());
        jsonObject.put("beCoverAble", beCoverAble);
        jsonObject.put("id", id);
        jsonObject.put("class", this.getClass().getName());
        return jsonObject;
    }

    public void resume(JSONObject jsonObject) {
        String str = jsonObject.getObject("position", String.class);
        str = str.substring(1, str.length() - 1);
        setPosition( Position.getPosition(Integer.parseInt(str.split(",")[0]), Integer.parseInt(str.split(",")[1])));
        beCoverAble = jsonObject.getObject("beCoverAble", Boolean.class);
        id = jsonObject.getObject("id", Integer.class);
    }

}
