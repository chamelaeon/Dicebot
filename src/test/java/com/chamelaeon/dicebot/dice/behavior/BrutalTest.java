package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BrutalTest {
    @Test
    public void testToString() {
        Behavior b = Brutal.getFactory().createBehavior(10);
        assertEquals("b10", b.toString());
    }

    @Test
    public void testGetFactory() {
        Behavior b = Brutal.getFactory().createBehavior(10);
        assertTrue(b instanceof Brutal);
        assertEquals(10, b.getThreshold().intValue());
    }
    
    @Test
    public void testForceGoodValue() {
        Brutal b = (Brutal) Brutal.getFactory().createBehavior(10);
        assertTrue(b.forceGoodValue());
    }
}
