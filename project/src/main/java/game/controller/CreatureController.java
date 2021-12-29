package game.controller;

import game.graphic.creature.Controllable;

abstract public class CreatureController{
    protected Controllable controllable;

    public CreatureController(){

    }

    public void setThing(Controllable controllable){
        this.controllable = controllable;
    }

    public abstract void stop();

    public abstract void start();
}
