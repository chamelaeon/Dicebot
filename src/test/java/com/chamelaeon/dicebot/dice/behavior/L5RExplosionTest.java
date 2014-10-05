package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class L5RExplosionTest {
    @Test
    public void testToString() {
        Behavior l5r = new L5RExplosion();
        assertEquals("", l5r.toString());
    }

    @Test
    public void testConstructor() {
        L5RExplosion explosion = new L5RExplosion();
        assertEquals(10, explosion.getThreshold().intValue());
    }
}
