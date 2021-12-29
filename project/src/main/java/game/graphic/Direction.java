package game.graphic;

import com.pFrame.Position;
import game.Location;

public class Direction {
    public static double Up = Math.PI / 2;
    public static double Right = 0;
    public static double Down = Math.PI * 3 / 2;
    public static double Left = Math.PI;

    public static double calDirection(Position src,Position dest){
        double angle;
        if (dest.getY() == src.getY() && src.getX() < dest.getX()) {
            angle = Direction.Down;
        } else if (dest.getY() == src.getY() && src.getX() > dest.getX()) {
            angle = Direction.Up;
        } else if (src.getX() == dest.getX() && src.getY() > dest.getY()) {
            angle = Direction.Left;
        } else if (src.getX() == dest.getX() && src.getY() < dest.getY()) {
            angle = Direction.Right;
        } else {
            angle = Math.atan(((double) dest.getX() - src.getX()) / (src.getY() - dest.getY()));
            if (angle > 0 && src.getX() < dest.getX()) {
                angle += Math.PI;
            } else if (angle < 0 && src.getX() > dest.getX()) {
                angle += Math.PI;
            }
        }
        return angle;
    }

    public static double calDirection(Location src,Location dest){
        double angle;
        if (dest.y() == src.y() && src.x() < dest.x()) {
            angle = Direction.Down;
        } else if (dest.y() == src.y() && src.x() > dest.x()) {
            angle = Direction.Up;
        } else if (src.x() == dest.x() && src.y() > dest.y()) {
            angle = Direction.Left;
        } else if (src.x() == dest.x() && src.y() < dest.y()) {
            angle = Direction.Right;
        } else {
            angle = Math.atan(((double) dest.x() - src.x()) / (src.y() - dest.y()));
            if (angle > 0 && src.x() < dest.x()) {
                angle += Math.PI;
            } else if (angle < 0 && src.x() > dest.x()) {
                angle += Math.PI;
            }
        }
        return angle;
    }
}