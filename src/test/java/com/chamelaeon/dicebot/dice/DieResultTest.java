package com.chamelaeon.dicebot.dice;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DieResultTest {

    DieResult dieResult;
    int result = 5;
    boolean wasRerolled = false;
    
    @Before
    public void setUp() throws Exception {
        dieResult = new DieResult(result, wasRerolled);
    }

    @Test
    public void testGetResult() {
        assertEquals(result, dieResult.getResult());
    }

    @Test
    public void testWasRerolled() {
        assertFalse(dieResult.wasRerolled());
    }

    @Test
    public void testCompareTo() {
        assertTrue(dieResult.compareTo(new DieResult(3, false)) > 0);
        assertEquals(0, dieResult.compareTo(dieResult));
        assertTrue(new DieResult(3, false).compareTo(dieResult) < 0);
    }

    @Test
    public void testToString() {
        assertEquals("5", dieResult.toString());
    }
    
    @Test
    public void testToStringWithReroll() {
        assertEquals("5*", new DieResult(5, true).toString());
    }

}
