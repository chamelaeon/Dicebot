package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MasteryTest {
    @Test
    public void testToString() {
        Behavior m = Mastery.getFactory().createBehavior(10);
        assertEquals("m", m.toString());
    }

    @Test
    public void testGetFactory() {
        Behavior m = Mastery.getFactory().createBehavior(10);
        assertTrue(m instanceof Mastery);
        assertEquals(9, m.getThreshold().intValue());
    }
}
