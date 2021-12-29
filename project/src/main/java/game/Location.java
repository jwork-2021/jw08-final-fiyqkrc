package game;

import com.pFrame.Position;
import game.world.World;

public record Location(int x, int y) {

    public boolean isEqualWith(Location location) {
        return (location.y == this.y) && (location.x == this.x);
    }

    @Override
    public String toString() {
        return String.format("Location{%d, %d}",this.x,this.y);
    }

    public Position getCentralPosition(){
        return Position.getPosition(x*World.tileSize+ World.tileSize/2,y*World.tileSize+World.tileSize/2);
    }
}
