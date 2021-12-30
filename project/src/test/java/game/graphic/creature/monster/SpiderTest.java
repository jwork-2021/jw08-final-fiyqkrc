package game.graphic.creature.monster;

import org.junit.Test;

import game.graphic.creature.operational.Calabash;

public class SpiderTest {
    @Test
    public void testResponseToEnemy() {
        Spider spider=new Spider();
        spider.aim=new Calabash();
        spider.responseToEnemy();
    }
}
