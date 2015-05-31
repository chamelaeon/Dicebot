package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractRerollTest {

    AbstractReroll reroll;
    
    @Before
    public void setUp() throws Exception {
        reroll = new AbstractReroll(3) {
            @Override
            public boolean forceGoodValue() {
                return false;
            }
        };
    }

    @Test
    public void testNeedsRerolled() {
        assertTrue(reroll.needsRerolled(1));
        assertTrue(reroll.needsRerolled(3));
        assertFalse(reroll.needsRerolled(4));
        assertFalse(reroll.needsRerolled(10));
    }

    @Test
    public void testCannotBeSatisfied() {
        assertFalse(reroll.cannotBeSatisfied(10));
        assertFalse(reroll.cannotBeSatisfied(4));
        assertTrue(reroll.cannotBeSatisfied(3));
        assertTrue(reroll.cannotBeSatisfied(1));
    }
}
