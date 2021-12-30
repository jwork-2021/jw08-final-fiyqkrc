package game.graphic.creature.operational;

import com.alibaba.fastjson.JSONObject;
import com.pFrame.Position;

import org.junit.Before;
import org.junit.Test;

import game.controller.KeyBoardController;
import game.graphic.drop.buff.Addition;
import game.graphic.drop.buff.AttackBuff;

import static org.junit.jupiter.api.Assertions.*;

public class CalabashTest {
    Calabash calabash;

    @Before
    public void setUp(){
        calabash=new Calabash();
    }

    @Test
    public void testContinue() {
        calabash.pause();
        calabash.Continue();
    }

    @Test
    public void testAddAddition() throws InterruptedException {
        calabash.setPosition(Position.getPosition(0, 0));
        calabash.addAddition(new Addition(new AttackBuff(), calabash ,-100, 100, 100, 0.5));
        assertNotEquals(calabash.getAttack(), calabash.getAttackLimit(), 0.0);
        assertNotEquals(calabash.getHealth(), calabash.getHealthLimit(), 0.0);
        assertTrue(calabash.getAdditions().isEmpty());
        assertNotEquals(calabash.getSpeed(), calabash.getSpeedLimit());
    }

    @Test
    public void testAddAttack() {
        Calabash calabash=new Calabash();
        double attack=calabash.getAttack();
        calabash.addAttack(0.5);
        assertEquals(calabash.getAttack(), attack+0.5);
    }

    @Test
    public void testAddCoin() {
        int coin=calabash.getCoin();
        calabash.addCoin(10);
        assertEquals(10+coin, calabash.getCoin());
    }

    @Test
    public void testAddSpeed() {
        int speed=calabash.getSpeed();
        calabash.addSpeed(10);
        assertEquals(10+speed, calabash.getSpeed());
    }

    @Test
    public void testDeHealth() {
        double health=calabash.getHealth();
        calabash.deHealth(10);
        assertNotEquals(health, calabash.getHealth());
        calabash.deHealth(-10-calabash.getHealthLimit());
        assertTrue(calabash.getHealth()<=calabash.getHealthLimit());
    }

    @Test
    public void testDead() {
        calabash.dead();
    }

    @Test
    public void testIsDead() {
        assertFalse(calabash.isDead());
    }

    @Test
    public void testMove() {
        calabash.move(0);
    }

    @Test
    public void testPause() {
        calabash.pause();
        calabash.Continue();
        calabash.setController(new KeyBoardController());
        calabash.pause();
    }

    @Test
    public void testSaveState() {
        JSONObject jsonObject=calabash.saveState();
        calabash.resumeState(jsonObject);
        assertEquals(jsonObject, calabash.saveState());
    }

    @Test
    public void testSearchAim() {
        assertNull(calabash.searchAim());
    }
}
