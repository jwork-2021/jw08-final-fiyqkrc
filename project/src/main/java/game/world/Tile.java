package game.world;

import game.Location;
import game.graphic.Thing;

public class Tile<T extends Thing> {

    private T thing;
    private Location location;

    public Tile(Location location){
        this.location=location;
    }

    public T getThing() {
        return thing;
    }

    public void setThing(T thing) {
        this.thing = thing;
        if(thing!=null)
            this.thing.setTile(this);
    }

    public Location getLocation(){
        return location;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}
