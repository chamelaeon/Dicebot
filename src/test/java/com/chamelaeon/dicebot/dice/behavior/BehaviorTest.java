package com.chamelaeon.dicebot.dice.behavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.dice.behavior.Behavior.BehaviorsPair;

public class BehaviorTest {

    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParseBehaviorWithNull() throws InputException {
        BehaviorsPair pair = Behavior.parseBehavior(null, -1);
        assertNull(pair.reroll);
        assertNull(pair.explosion);
    }
    
    @Test
    public void testParseBehaviorWithBlank() throws InputException {
        BehaviorsPair pair = Behavior.parseBehavior("", -1);
        assertNull(pair.reroll);
        assertNull(pair.explosion);
    }
    
    @Test
    public void testParseBadReroll() throws InputException {
        BehaviorsPair pair = Behavior.parseBehavior("qqq", -1);
        assertNull(pair.reroll);
        assertNull(pair.explosion);
    }
    
    @Test
    public void testParseBehaviorWithReroll() throws InputException {
        Reroll reroll = Behavior.parseBehavior("e", 1).reroll;
        assertEquals(Emphasis.class, reroll.getClass());
    }
    
    @Test
    public void testParseBehaviorWithOneDigitReroll() throws InputException {
        Reroll reroll = Behavior.parseBehavior("b2", 6).reroll;
        assertEquals(Brutal.class, reroll.getClass());
        assertEquals(2, reroll.getThreshold().intValue());
    }
    
    @Test
    public void testParseBehaviorWithTwoDigitReroll() throws InputException {
        Reroll reroll = Behavior.parseBehavior("b10", 12).reroll;
        assertEquals(Brutal.class, reroll.getClass());
        assertEquals(10, reroll.getThreshold().intValue());
    }
    
    @Test
    public void testParseBehaviorWithExplosion() throws InputException {
        Explosion explosion = Behavior.parseBehavior("v", 12).explosion;
        assertEquals(Vorpal.class, explosion.getClass());
        assertEquals(12, explosion.getThreshold().intValue());
    }
    
    @Test
    public void testParseBehaviorWithOneDigitExplosion() throws InputException {
        Explosion explosion = Behavior.parseBehavior("v8", 0).explosion;
        assertEquals(Vorpal.class, explosion.getClass());
        assertEquals(8, explosion.getThreshold().intValue());
    }
    
    @Test
    public void testParseBehaviorWithTwoDigitExplosion() throws InputException {
        Explosion explosion = Behavior.parseBehavior("v12", 0).explosion;
        assertEquals(Vorpal.class, explosion.getClass());
        assertEquals(12, explosion.getThreshold().intValue());
    }

    @Test
    public void testParseBehaviorWithMultipleReroll() throws InputException {
        BehaviorsPair pair =  Behavior.parseBehavior("eb", 2);
        Reroll reroll = pair.reroll;
        assertEquals(Emphasis.class, reroll.getClass());
        assertEquals(1, reroll.getThreshold().intValue());
    }
    
    @Test
    public void testParseBehaviorWithMultipleExplosion() throws InputException {
        BehaviorsPair pair =  Behavior.parseBehavior("vm", 12);
        Explosion explosion = pair.explosion;
        assertEquals(Vorpal.class, explosion.getClass());
        assertEquals(12, explosion.getThreshold().intValue());
    }
    
    @Test
    public void testParseBehaviorWithBoth() throws InputException {
        BehaviorsPair pair =  Behavior.parseBehavior("em", 100);
        Explosion explosion = pair.explosion;
        Reroll reroll = pair.reroll;
        assertEquals(Mastery.class, explosion.getClass());
        assertEquals(9, explosion.getThreshold().intValue());
        assertEquals(Emphasis.class, reroll.getClass());
        assertEquals(1, reroll.getThreshold().intValue());
    }
    
    @Test
    public void testGetPrettyString() throws InputException {
        BehaviorsPair pair = Behavior.parseBehavior("me", 10);
        Explosion explosion = pair.explosion;
        Reroll reroll = pair.reroll;
        
        assertEquals("em", Behavior.getPrettyString(reroll, explosion));
    }
    
    @Test
    public void testGetPrettyStringWithNulls() throws InputException {
        assertEquals("", Behavior.getPrettyString(null, null));
    }
}
