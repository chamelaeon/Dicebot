package com.chamelaeon.dicebot.dice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;

public class ModifierTest {

    Modifier mod;
    Personality personality;
    
    @Before
    public void setUp() throws Exception {
        mod = new Modifier(10) {
            @Override
            public Modifier appendToValue(int delta) {
                return null;
            }
        }; 
        personality = mock(Personality.class);
        when(personality.parseShort("3")).thenReturn((short) 3);
        when(personality.getException("BrokenRegexp")).thenReturn(new InputException("I did a bad. :C"));
    }

    @Test
    public void testApply() {
        assertEquals(15, mod.apply(5));
    }
   
    @Test
    public void testCreateModifierPos() throws InputException {
        Modifier posMod = Modifier.createModifier("+3", personality);
        assertEquals(3, posMod.apply(0));
    }
    
    @Test
    public void testCreateModifierNeg() throws InputException {
        Modifier negMod = Modifier.createModifier("-3", personality);
        assertEquals(-3, negMod.apply(0));
    }
    
    @Test
    public void testCreateModifierNull() throws InputException {
        Modifier nullMod = Modifier.createModifier(null, personality);
        assertEquals(0, nullMod.apply(0));
    }
    
    @Test(expected = InputException.class)
    public void testCreateModifierBad() throws InputException {
        Modifier.createModifier("badnum", personality);
    }

    @Test
    public void testAppendToValuePos() throws InputException {
        Modifier posMod = Modifier.createModifier("+3", personality);

        assertEquals(6, posMod.appendToValue(3).apply(0));
        assertEquals(0, posMod.appendToValue(-3).apply(0));
        assertEquals(3, posMod.appendToValue(0).apply(0));
    }
    
    @Test
    public void testAppendToValueNeg() throws InputException {
        Modifier negMod = Modifier.createModifier("-3", personality);

        assertEquals(0, negMod.appendToValue(3).apply(0));
        assertEquals(-6, negMod.appendToValue(-3).apply(0));
        assertEquals(-3, negMod.appendToValue(0).apply(0));
    }
    
    @Test
    public void testAppendToValueNull() throws InputException {
        Modifier nullMod = Modifier.createModifier(null, personality);

        assertEquals(3, nullMod.appendToValue(3).apply(0));
        assertEquals(-3, nullMod.appendToValue(-3).apply(0));
        assertEquals(0, nullMod.appendToValue(0).apply(0));
    }
    
    @Test
    public void testCreateNullModifier() {
        Modifier mod = Modifier.createNullModifier();
        assertEquals(0, mod.apply(0));
    }

    @Test
    public void testToStringPos() throws InputException {
        Modifier posMod = Modifier.createModifier("+3", personality);
        assertEquals("+3", posMod.toString());
    }
    
    @Test
    public void testToStringNeg() throws InputException {
        Modifier negMod = Modifier.createModifier("-3", personality);
        assertEquals("-3", negMod.toString());
    }
    
    @Test
    public void testToStringNull() throws InputException {
        Modifier nullMod = Modifier.createModifier(null, personality);
        assertEquals("", nullMod.toString());
    }

}
