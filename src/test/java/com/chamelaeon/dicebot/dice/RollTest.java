package com.chamelaeon.dicebot.dice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;

public class RollTest {

    Roll roll;
    short rolled = 5;
    short kept = 5;
    Die die;
    Modifier modifier; 
    Reroll reroll;
    Explosion explosion;
    Personality personality;
    
    @Before
    public void setUp() throws Exception {
        die = mock(Die.class);
        modifier = mock(Modifier.class);
        reroll = mock(Reroll.class);
        explosion = mock(Explosion.class);
        personality = mock(Personality.class);
        
        when(die.getSides()).thenReturn((short) 8);
        when(reroll.cannotBeSatisfied(8)).thenReturn(false);
        when(explosion.explodesInfinitely(1)).thenReturn(false);
        
        roll = new Roll(rolled, kept, die, modifier, reroll, explosion, personality);
        
        verify(reroll).cannotBeSatisfied(8);
        verify(die).getSides();
        verify(explosion).explodesInfinitely(1);
    }
    
    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(die);
        verifyNoMoreInteractions(modifier, reroll, explosion, personality);
    }

    @Test(expected = InputException.class)
    public void testRollCannotSatisfyRerollWith1Rolled() throws InputException {
        try {
            rolled = 1;
            when(reroll.cannotBeSatisfied(8)).thenReturn(true);
            when(personality.getException("CannotSatisfyRerollSingleDie", new TokenSubstitution("%REROLL%", reroll), 
                            new TokenSubstitution("%SIDES%", 8))).thenReturn(new InputException("badmessage"));
            
            roll = new Roll(rolled, kept, die, modifier, reroll, explosion, personality);
        } finally {
            verify(die, times(3)).getSides();
            verify(reroll, times(2)).cannotBeSatisfied(8);
            verify(personality).getException("CannotSatisfyRerollSingleDie", new TokenSubstitution("%REROLL%", reroll), 
                            new TokenSubstitution("%SIDES%", 8));
        }
    }
    
    @Test(expected = InputException.class)
    public void testRollCannotSatisfyRerollWithMultipleRolled() throws InputException {
        try {
            when(reroll.cannotBeSatisfied(8)).thenReturn(true);
            when(personality.getException("CannotSatisfyRerollMultipleDice", new TokenSubstitution("%REROLL%", reroll), 
                    new TokenSubstitution("%DICEROLLED%", rolled), new TokenSubstitution("%SIDES%", 8))).thenReturn(
                            new InputException("badmessage"));
            
            roll = new Roll(rolled, kept, die, modifier, reroll, explosion, personality);
        } finally {
            verify(die, times(3)).getSides();
            verify(reroll, times(2)).cannotBeSatisfied(8);
            verify(personality).getException("CannotSatisfyRerollMultipleDice", new TokenSubstitution("%REROLL%", reroll), 
                    new TokenSubstitution("%DICEROLLED%", rolled), new TokenSubstitution("%SIDES%", 8));
        }
    }
    
    @Test(expected = InputException.class)
    public void testRollExplodesInfinitely() throws InputException {
        try {
            when(reroll.cannotBeSatisfied(8)).thenReturn(false);
            when(personality.getException("InfiniteExplosion")).thenReturn(new InputException("badmessage"));
            when(explosion.explodesInfinitely(1)).thenReturn(true);
            
            roll = new Roll(rolled, kept, die, modifier, reroll, explosion, personality);
        } finally {
            verify(die, times(2)).getSides();
            verify(reroll, times(2)).cannotBeSatisfied(8);
            verify(personality).getException("InfiniteExplosion");
            verify(explosion, times(2)).explodesInfinitely(1);
        }
    }

    @Test
    public void testToString() throws InputException {
        String string = roll.toString();
        assertTrue(string.startsWith("Roll [rolled=5, kept=5, die=Mock for Die, hashCode:")); 
        assertTrue(string.contains("modifier=Mock for Modifier, hashCode:"));
        assertTrue(string.contains("reroll=Mock for Reroll, hashCode:")); 
        assertTrue(string.contains("explosion=Mock for Explosion, hashCode:"));
        assertTrue(string.contains("personality=Mock for Personality, hashCode")); 
        
        roll = new Roll((short) 1, (short) 1, die, null, null, null, null);
        assertTrue(roll.toString().startsWith("Roll [rolled=1, kept=1, die=Mock for Die, hashCode:"));
    }

    @Test
    public void testAlterValuesIntIntModifier() throws InputException {
        Roll roll2 = roll.alterValues(12, 10, null);
        assertEquals(12, roll2.getRolled());
        assertEquals(10, roll2.getKept());
        assertNull(roll2.getModifier());
        assertNotSame(roll, roll2);
        
        verify(die, times(2)).getSides();
        verify(reroll, times(2)).cannotBeSatisfied(8);
        verify(explosion, times(2)).explodesInfinitely(1);
    }

    @Test
    public void testAlterValuesShortShortModifier() throws InputException {
        Roll roll2 = roll.alterValues((short) 12, (short) 10, null);
        assertEquals(12, roll2.getRolled());
        assertEquals(10, roll2.getKept());
        assertNull(roll2.getModifier());
        assertNotSame(roll, roll2);
        
        verify(die, times(2)).getSides();
        verify(reroll, times(2)).cannotBeSatisfied(8);
        verify(explosion, times(2)).explodesInfinitely(1);
    }

    @Test
    public void testGetRolled() {
        assertEquals(rolled, roll.getRolled());
    }

    @Test
    public void testGetKept() {
        assertEquals(kept, roll.getKept());
    }

    @Test
    public void testGetDie() {
        assertSame(die, roll.getDie());
    }

    @Test
    public void testGetModifier() {
        assertSame(modifier, roll.getModifier());
    }

    @Test
    public void testGetReroll() {
        assertSame(reroll, roll.getReroll());
    }

    @Test
    public void testGetExplosion() {
        assertSame(explosion, roll.getExplosion());
    }

    @Test
    public void testPerformRoll() {
        Statistics statistics = mock(Statistics.class);
        Random random = mock(Random.class);
        
        when(die.rollDie(random, reroll, explosion, statistics)).thenReturn(new DieResult(8, false));
        
        List<GroupResult> result = roll.performRoll(1, random, statistics);
        assertEquals(40, result.get(0).getNatural());
        assertFalse(result.get(0).isCriticalSuccess());
        assertFalse(result.get(0).isCriticalFailure());
        
        verify(die, times(5)).rollDie(random, reroll, explosion, statistics);
        verify(die).isCritSuccess(5, 40);
        verify(modifier).apply(40);
    }

    @Test
    public void testPerformRollCritSuccess() {
        Statistics statistics = mock(Statistics.class);
        Random random = mock(Random.class);
        
        when(die.rollDie(random, reroll, explosion, statistics)).thenReturn(new DieResult(8, false));
        when(die.isCritSuccess(5, 40)).thenReturn(true);
        when(personality.useCritSuccesses()).thenReturn(true);
        
        List<GroupResult> result = roll.performRoll(1, random, statistics);
        assertEquals(40, result.get(0).getNatural());
        assertTrue(result.get(0).isCriticalSuccess());
        assertFalse(result.get(0).isCriticalFailure());
        
        verify(die, times(5)).rollDie(random, reroll, explosion, statistics);
        verify(die).isCritSuccess(5, 40);
        verify(modifier).apply(40);
        verify(personality).useCritSuccesses();
    }
    
    @Test
    public void testPerformRollCritFail() {
        Statistics statistics = mock(Statistics.class);
        Random random = mock(Random.class);
        
        when(die.rollDie(random, reroll, explosion, statistics)).thenReturn(new DieResult(1, false));
        when(personality.useCritFailures()).thenReturn(true);
        
        List<GroupResult> result = roll.performRoll(1, random, statistics);
        assertEquals(5, result.get(0).getNatural());
        assertFalse(result.get(0).isCriticalSuccess());
        assertTrue(result.get(0).isCriticalFailure());
        
        verify(die, times(5)).rollDie(random, reroll, explosion, statistics);
        verify(modifier).apply(5);
        verify(personality).useCritFailures();
    }
}
