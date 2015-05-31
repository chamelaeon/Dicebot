package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VorpalTest {
    @Test
    public void testToString() {
        Behavior v = Vorpal.getFactory().createBehavior(10);
        assertEquals("v10", v.toString());
    }

    @Test
    public void testGetFactory() {
        Behavior v = Vorpal.getFactory().createBehavior(10);
        assertTrue(v instanceof Vorpal);
        assertEquals(10, v.getThreshold().intValue());
    }
}
