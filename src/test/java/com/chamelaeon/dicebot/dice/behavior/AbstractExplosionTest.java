package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractExplosionTest {
    AbstractExplosion explosion;
    
    @Before
    public void setUp() throws Exception {
        explosion = new AbstractExplosion(10) {
        };
    }

    @Test
    public void testNeedsRerolled() {
        assertFalse(explosion.shouldExplode(1));
        assertFalse(explosion.shouldExplode(3));
        assertFalse(explosion.shouldExplode(4));
        assertTrue(explosion.shouldExplode(10));
    }

    @Test
    public void testExplodesInfinitely() {
        assertFalse(explosion.explodesInfinitely(1));
        assertFalse(explosion.explodesInfinitely(9));
        assertTrue(explosion.explodesInfinitely(10));
        assertTrue(explosion.explodesInfinitely(11));
    }
}
