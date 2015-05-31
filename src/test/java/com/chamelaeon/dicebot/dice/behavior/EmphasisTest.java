package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EmphasisTest {
    @Test
    public void testToString() {
        Behavior e = Emphasis.getFactory().createBehavior(10);
        assertEquals("e", e.toString());
    }

    @Test
    public void testGetFactory() {
        Behavior e = Emphasis.getFactory().createBehavior(10);
        assertTrue(e instanceof Emphasis);
        assertEquals(1, e.getThreshold().intValue());
    }
    
    @Test
    public void testForceGoodValue() {
        Emphasis e = (Emphasis) Emphasis.getFactory().createBehavior(10);
        assertFalse(e.forceGoodValue());
    }
}
