package game;

import java.util.ArrayList;

public class Attack {
    final public static int HIT=1;
    final public static int FIRE=2;
    final public static int ICE=3;
    final public static int BOMB=4;

    public int kind;
    public ArrayList<Location> affectedTiles;
    public double attackNumber;
    public int group;

    public Attack(int kind,ArrayList<Location> affectedTiles,double attackNumber,int group){
        this.kind=kind;
        this.affectedTiles=affectedTiles;
        this.attackNumber=attackNumber;
        this.group=group;
    }
}
