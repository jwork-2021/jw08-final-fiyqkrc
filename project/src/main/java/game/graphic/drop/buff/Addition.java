package game.graphic.drop.buff;

import com.pFrame.PTimer;
import com.pFrame.PTimerTask;
import game.graphic.interactive.GameThread;
import game.graphic.creature.Creature;

public class Addition {
    final public double health;
    final public double attack;
    final public double resistance;
    final public double speed;
    final public boolean timeOnly;
    final public int time;
    final public Creature creature;
    final public Buff buff;

    public Addition(Buff buff,Creature creature,double health,double attack,double speed,double resistance,int time){
        this.health=health;
        this.attack=attack;
        this.resistance=resistance;
        this.speed=speed;
        this.time=time;
        this.timeOnly=true;
        this.creature=creature;
        this.buff=buff;
    }

    public Addition(Buff buff,Creature creature,double health,double attack,double speed,double resistance){
        this.health=health;
        this.attack=attack;
        this.resistance=resistance;
        this.speed=speed;
        this.time=0;
        this.timeOnly=false;
        this.creature=creature;
        this.buff=buff;
    }

    public void useAddition(){
        if(timeOnly){
            this.creature.deHealth((-this.health)* creature.getHealthLimit());
            this.creature.addSpeed((int)(creature.getSpeedLimit()*speed));
            this.creature.addAttack(creature.getAttackLimit()*attack);
            this.creature.addResistance(resistance);
            this.creature.getAdditions().remove(this);
            PTimer pTimer=new PTimer();
            pTimer.schedule(new Task(this),false,time);
            Thread thread=new Thread(pTimer);
            GameThread.threadSet.add(thread);
            thread.start();
        }
        else{
            this.creature.deHealth((-this.health)* creature.getHealthLimit());
            this.creature.addSpeed((int)(creature.getSpeedLimit()*speed));
            this.creature.addAttack(creature.getAttackLimit()*attack);
            this.creature.addResistance(resistance);
            this.creature.getAdditions().remove(this);
        }
    }

    class Task implements PTimerTask{
        Addition addition;

        public Task(Addition addition){
            this.addition=addition;
        }

        @Override
        public void doTask() {
            creature.deHealth((health)* creature.getHealthLimit());
            creature.addSpeed(-(int)(creature.getSpeedLimit()*speed));
            creature.addAttack(-creature.getAttackLimit()*attack);
            creature.addResistance(-resistance);
        }
    }
}
