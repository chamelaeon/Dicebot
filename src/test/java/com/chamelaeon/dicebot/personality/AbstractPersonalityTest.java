package com.chamelaeon.dicebot.personality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.TokenSubstitution;

public class AbstractPersonalityTest {

    AbstractPersonality personality;
    String key = "key";
    String tokenKey = "tokenKey";
    String value = "value";
    String token = "%TOKEN%";
    String tokenValue = value + token;
   
    @Before
    public void setUp() throws Exception {
        personality = new AbstractPersonality() {
        };
        personality.outputTexts.put(key, value);
        personality.outputTexts.put(tokenKey, tokenValue);
    }

    @Test
    public void testGetExceptionNoArray() {
        InputException ie = personality.getException(key);
        assertSame(value, ie.getMessage());
    }
    
    @Test
    public void testGetException() {
        int sub = 5;
        InputException ie = personality.getException(tokenKey, new TokenSubstitution(token, sub));
        assertEquals(value + sub, ie.getMessage());
    }

    @Test
    public void testGetMessageString() {
        int sub = 5;
        assertEquals(value + sub, personality.getMessage(tokenKey, new TokenSubstitution(token, sub)));
    }

    @Test
    public void testGetMessageStringNoArray() {
        assertSame(value, personality.getMessage(key));
    }

    @Test
    public void testGetRollResult() {
        int sub = 5;
        assertEquals(value + sub, personality.getRollResult(tokenKey, new TokenSubstitution(token, sub)));
    }

    @Test
    public void testUseCritSuccesses() {
        assertTrue(personality.useCritSuccesses.get());
    }

    @Test
    public void testUseCritFailures() {
        assertTrue(personality.useCritSuccesses.get());
    }

    @Test
    public void testChooseCriticalFailureLine() {
        String critFail = "CRITFAIL";
        personality.criticalFailures.add(critFail);
        
        assertSame(critFail, personality.chooseCriticalFailureLine());
    }

    @Test
    public void testChooseCriticalSuccessLine() {
        String critSucc = "CRITSUCC";
        personality.criticalSuccesses.add(critSucc);
        
        assertSame(critSucc, personality.chooseCriticalSuccessLine());
    }

    @Test
    public void testParseShort() throws InputException {
        assertEquals(5, personality.parseShort("5"));
    }
    
    @Test(expected = InputException.class)
    public void testParseShortBad() throws InputException {
        personality.parseShort("notastring");
    }


    @Test
    public void testParseDiceCountNull() throws InputException {
        assertEquals(1, personality.parseDiceCount(null));
    }
    
    @Test
    public void testParseDiceCountEmpty() throws InputException {
        assertEquals(1, personality.parseDiceCount(""));
    }
    
    @Test
    public void testParseDiceCount() throws InputException {
        assertEquals(5, personality.parseDiceCount("5"));
    }
    
    @Test
    public void testPerformTokenSubstitution() {
        
    }
}
