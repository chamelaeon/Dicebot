package com.chamelaeon.dicebot.dice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;
import com.chamelaeon.dicebot.statistics.NullStatistics;

public class DieTest {

    Die fudgeDie;
    Die simpleDie;
    short sides = 12;
    Statistics stats;
    Random random;
    
    @Before
    public void setUp() throws Exception {
        fudgeDie = new FudgeDie();
        simpleDie = new SimpleDie(sides);
        stats = new NullStatistics();
        random = new Random() {
            int count = 0;
            @Override
            public int getRoll(int diceType, Statistics statistics) {
                count++;
                 if (count == 1) {
                     return 1;
                 } else if (count == 2) {
                     return 1;
                 } else if (count == 3) {
                     return 4;
                 } else if (count == 4) {
                     return 6;
                 }
                 return 5;
            }
            
            @Override
            public int getRoll(int diceType) {
                return 0; // Ignore.
            }
        };
    }

    @Test
    public void testGetSidesSimple() {
        assertEquals(sides, simpleDie.getSides());
    }
    
    @Test
    public void testGetSidesFudge() {
        assertEquals(6, fudgeDie.getSides());
    }

    @Test
    public void testIsCritSuccessSimple() {
        assertTrue(simpleDie.isCritSuccess(1, 12));
        assertTrue(simpleDie.isCritSuccess(2, 24));
        assertFalse(simpleDie.isCritSuccess(1, 1));
        assertFalse(simpleDie.isCritSuccess(1, 11));
        assertFalse(simpleDie.isCritSuccess(1, 13));
    }
    
    @Test
    public void testIsCritSuccessFudge() {
        assertFalse(fudgeDie.isCritSuccess(1, 6));
    }

    @Test
    public void testRollDieFudge() {
        // Fudge has no rerolls or explosions.
    	List<DieResult> result = fudgeDie.rollDice(1, random, null, null, stats);
        assertEquals(-1, result.get(0).getResult());
        result = fudgeDie.rollDice(1, random, null, null, stats);
        assertEquals(-1, result.get(0).getResult());
        result = fudgeDie.rollDice(1, random, null, null, stats);
        assertEquals(0, result.get(0).getResult());
        result = fudgeDie.rollDice(1, random, null, null, stats);
        assertEquals(1, result.get(0).getResult());
    }
    
    @Test
    public void testRollDieSimple() {
    	List<DieResult> result = simpleDie.rollDice(1, random, null, null, stats);
        assertEquals(1, result.get(0).getResult());
    }
    
    @Test
    public void testRollDieWithReroll() {
        Reroll simpleReroll = mock(Reroll.class);
        when(simpleReroll.needsRerolled(1)).thenReturn(true);
        when(simpleReroll.forceGoodValue()).thenReturn(false);
        
        // The first roll of 1 is skipped and we get the second roll.
        List<DieResult> result = simpleDie.rollDice(1, random, simpleReroll, null, stats);
        assertEquals(1, result.get(0).getResult());
        result = simpleDie.rollDice(1, random, simpleReroll, null, stats);
        assertEquals(4, result.get(0).getResult());
        
        verify(simpleReroll, times(2)).needsRerolled(1);
        verify(simpleReroll).needsRerolled(4);
        verify(simpleReroll).forceGoodValue();
        verifyNoMoreInteractions(simpleReroll);
    }
    
    @Test
    public void testRollDieWithRepeatingReroll() {
        Reroll repeatingReroll = mock(Reroll.class);
        when(repeatingReroll.needsRerolled(1)).thenReturn(true);
        when(repeatingReroll.needsRerolled(4)).thenReturn(true);
        when(repeatingReroll.needsRerolled(5)).thenReturn(false);
        when(repeatingReroll.needsRerolled(6)).thenReturn(false);
        when(repeatingReroll.forceGoodValue()).thenReturn(true);
        
        List<DieResult> result = simpleDie.rollDice(1, random, repeatingReroll, null, stats);
        assertEquals(6, result.get(0).getResult());
        result = simpleDie.rollDice(1, random, repeatingReroll, null, stats);
        assertEquals(5, result.get(0).getResult());
        
        verify(repeatingReroll, times(2)).needsRerolled(1);
        verify(repeatingReroll).needsRerolled(4);
        verify(repeatingReroll).needsRerolled(6);
        verify(repeatingReroll).needsRerolled(5);
        verify(repeatingReroll, times(2)).forceGoodValue();
        verifyNoMoreInteractions(repeatingReroll);
    }
    
    @Test
    public void testRollDieWithExplosion() {
        Explosion simpleExplosion = mock(Explosion.class);
        when(simpleExplosion.shouldExplode(1)).thenReturn(true).thenReturn(false);
        
        // It's 2 because we exploded once and rolled the other 1.
        List<DieResult> result = simpleDie.rollDice(1, random, null, simpleExplosion, stats);
        assertEquals(2, result.get(0).getResult());
        result = simpleDie.rollDice(1, random, null, simpleExplosion, stats);
        assertEquals(4, result.get(0).getResult());
        
        verify(simpleExplosion, times(2)).shouldExplode(1);
        verify(simpleExplosion).shouldExplode(4);
        verifyNoMoreInteractions(simpleExplosion);
    }

    @Test
    public void testRollDieWithMultipleExplosion() {
        Explosion simpleExplosion = mock(Explosion.class);
        when(simpleExplosion.shouldExplode(1)).thenReturn(true);
        
        // It's 6 because both 1s exploded and the next roll was a 4: 1 + 1 + 4.
        List<DieResult> result = simpleDie.rollDice(1, random, null, simpleExplosion, stats);
        assertEquals(6, result.get(0).getResult());
        result = simpleDie.rollDice(1, random, null, simpleExplosion, stats);
        assertEquals(6, result.get(0).getResult());
        
        verify(simpleExplosion, times(2)).shouldExplode(1);
        verify(simpleExplosion).shouldExplode(4);
        verify(simpleExplosion).shouldExplode(6);
        verifyNoMoreInteractions(simpleExplosion);
    }
}
