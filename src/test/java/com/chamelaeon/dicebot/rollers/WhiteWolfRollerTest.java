package com.chamelaeon.dicebot.rollers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.random.Random;

public class WhiteWolfRollerTest extends RollerTestBase {

	WhiteWolfRoller roller;
	Random random;
	Statistics statistics = mock(Statistics.class);
	
    @Before
    public void setUp() throws Exception {
        doPersonalitySetup();
        random = mock(Random.class);
        
        // Set up seven rolls - on a 7t2 we should see two 10s, two ones, and four total successes (without specialization).
        when(random.getRoll(10, statistics)).thenReturn(10, 10, 1, 1, 8, 8, 3);
        
        roller = new WhiteWolfRoller(personality, random);
    }

    @After
    public void tearDown() throws Exception {
    	doPersonalityTeardown();
    }
    
    @Test
    public void testBasicRegexp() {
        String regexp = WhiteWolfRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("7t2");
        m.find();
        assertEquals("7t2", m.group(0));
        assertEquals("7", m.group(1));
        assertEquals("2", m.group(2));
        assertNull(m.group(3));
        assertNull(m.group(4));
        assertNull(m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
    }
    
    @Test
    public void testtestBasicRollSuccessWithOriginalConstructor() throws InputException {
    	roller = new WhiteWolfRoller(personality);
    	
    	String[] parts = new String[] {"7t2", "7", "2", null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%SPECIALIZATION%", ""), tokenSubMatcher("%DCSTRING%", " "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "*"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "*"));
    }
    
    @Test
    public void testBasicRollSuccess() throws InputException {
        String[] parts = new String[] {"7t2", "7", "2", null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%SPECIALIZATION%", ""), tokenSubMatcher("%DCSTRING%", " "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "2"));
    }
    
    @Test(expected = InputException.class)
	public void testRollWithNoDice() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"0t2", "0", "2", null, null, null, null};
			when(personality.getException("Roll0Dice")).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseShort("0");
			verify(personality).parseShort("2");
			verify(personality).getException("Roll0Dice");
		}
	}
    
	@Test(expected = InputException.class)
	public void testRollWithNoDC() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"7t2 dc0", "7", "2", null, null, null, " dc0", "0"};
			when(personality.getException("DCEquals0")).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseShort("7");
			verify(personality).parseShort("2");
			verify(personality).parseShort("0");
			verify(personality).getException("DCEquals0");
		}
	}

	@Test(expected = InputException.class)
	public void testRollCannotSatisfySuccesses() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"6t7", "6", "7", null, null, null, null};
			when(personality.getException(eq("CannotSatisfySuccesses"), (TokenSubstitution[]) anyVararg())).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseShort("6");
			verify(personality).parseShort("7");
			verify(personality).getException(eq("CannotSatisfySuccesses"), 
					tokenSubMatcher("%SUCCESSESNEEDED%", "7"), tokenSubMatcher("%DICEROLLED%", "6"));
		}
	}
    
    @Test
    public void testBasicRollFailure() throws InputException {
        String[] parts = new String[] {"7t5", "7", "5", null, null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("WhiteWolfFailure"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "5"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%SPECIALIZATION%", ""), tokenSubMatcher("%DCSTRING%", " "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "-1"));
    }
    
    @Test
    public void testWithModifierRegexp() {
        String regexp = WhiteWolfRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("7t2+5");
        m.find();
        assertEquals("7t2+5", m.group(0));
        assertEquals("7", m.group(1));
        assertEquals("2", m.group(2));
        assertNull(m.group(3));
        assertEquals("+5", m.group(4));
        assertNull(m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
    }
    
    @Test
    public void testRollWithModifier() throws InputException {
        String[] parts = new String[] {"7t2+5", "7", "2", null, "+5", null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", "+5"), 
                tokenSubMatcher("%SPECIALIZATION%", ""), tokenSubMatcher("%DCSTRING%", " "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "7"));
    }
    
    @Test
    public void testWithCustomDCRegexp() {
        String regexp = WhiteWolfRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("7t2 dc9");
        m.find();
        assertEquals("7t2 dc9", m.group(0));
        assertEquals("7", m.group(1));
        assertEquals("2", m.group(2));
        assertNull(m.group(3));
        assertNull(m.group(4));
        assertNull(m.group(5));
        assertEquals(" dc9", m.group(6));
        assertEquals("9", m.group(7));
    }
    
    @Test
    public void testRollWithCustomDC() throws InputException {
        String[] parts = new String[] {"7t2 dc9", "7", "2", null, null, null, " dc9", "9"};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).parseShort("9");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%SPECIALIZATION%", ""), tokenSubMatcher("%DCSTRING%", " dc9 "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "0"));
    }
    
    @Test
    public void testSpecializationRegexp() {
        String regexp = WhiteWolfRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("7t2e");
        m.find();
        assertEquals("7t2e", m.group(0));
        assertEquals("7", m.group(1));
        assertEquals("2", m.group(2));
        assertEquals("e", m.group(3));
        assertNull(m.group(4));
        assertNull(m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
    }

    @Test
    public void testSpecializationAndModifierRegexp() {
        String regexp = WhiteWolfRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        // Ensure we test on both sides of the modifier.
        Matcher m = p.matcher("7t2+5e");
        m.find();
        assertEquals("7t2+5e", m.group(0));
        assertEquals("7", m.group(1));
        assertEquals("2", m.group(2));
        assertNull(m.group(3));
        assertEquals("+5", m.group(4));
        assertEquals("e", m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
        
        m = p.matcher("7t2e+5");
        m.find();
        assertEquals("7t2e+5", m.group(0));
        assertEquals("7", m.group(1));
        assertEquals("2", m.group(2));
        assertEquals("e", m.group(3));
        assertEquals("+5", m.group(4));
        assertNull(m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
    }
    
    @Test
    public void testRollWithTwoSpecializations() throws InputException {
        String[] parts = new String[] {"7t2+5", "7", "2", "e", "+5", "e", null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", "+5"), 
                tokenSubMatcher("%SPECIALIZATION%", "e"), tokenSubMatcher("%DCSTRING%", " "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "9"));
    }
    
    
    @Test
    public void testSpecializationRollOnLeftSide() throws InputException {
        String[] parts = new String[] {"7t2e", "7", "2", "e", null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%SPECIALIZATION%", "e"), tokenSubMatcher("%DCSTRING%", " "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "4"));
    }
    
    @Test
    public void testSpecializationRollOnRightSide() throws InputException {
        String[] parts = new String[] {"7t2e", "7", "2", null, null, "e", null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%SPECIALIZATION%", "e"), tokenSubMatcher("%DCSTRING%", " "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "4"));
    }
    
    @Test
    public void testKitchenSinkRegexp() {
        String regexp = WhiteWolfRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("7t2e+5 dc9");
        m.find();
        assertEquals("7t2e+5 dc9", m.group(0));
        assertEquals("7", m.group(1));
        assertEquals("2", m.group(2));
        assertEquals("e", m.group(3));
        assertEquals("+5", m.group(4));
        assertNull(m.group(5));
        assertEquals(" dc9", m.group(6));
        assertEquals("9", m.group(7));
    }
    
    @Test
    public void testRollWithKitchenSink() throws InputException {
        String[] parts = new String[] {"7t2+5e dc9", "7", "2", null, "+5", "e", " dc9", "9"};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("7");
        verify(personality).parseShort("2");
        verify(personality).parseShort("5");
        verify(personality).parseShort("9");
        verify(personality).getRollResult(eq("WhiteWolfSuccess"), tokenSubMatcher("%ROLLEDDICE%", "7"),
                tokenSubMatcher("%SUCCESSESNEEDED%", "2"), tokenSubMatcher("%MODIFIER%", "+5"), 
                tokenSubMatcher("%SPECIALIZATION%", "e"), tokenSubMatcher("%DCSTRING%", " dc9 "),
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%DICEVALUE%", "*"), 
                tokenSubMatcher("%ONESROLLED%", "2"), tokenSubMatcher("%SUCCESSESOVERMINIMUM%", "7"));
    }
  
    @Test
    public void testHelpDetails() {
        HelpDetails details = roller.getHelpDetails();
        assertEquals("White Wolf", details.getCommandName());
        assertEquals("A dice roller for White Wolf (roll/successes style), which rolls a number of d10s and "
        		+ "looks for numbers over a certain value, attempting to accumulate a certain number of successes. "
        		+ "A number of fixed successes can be added or removed. Specifying e (for emphasis) makes 10s "
        		+ "explode twice. An example: 6t2+1e dc7 - this specifies rolling 6 dice, looking for 2 dice with a "
        		+ "value of 7 or higher. Tens will explode twice, and there will be one guaranteed success. If not "
        		+ "specified, the DC is 6+.", 
        		details.getDescription());
        assertEquals("roller", details.getType());

        List<String> list = new ArrayList<>();
        list.add("Basic roll: 7t2");
        list.add("Roll with 2 fixed successes: 7t2+2");
        list.add("Roll with 10s counting twice: 7t2e");
        list.add("Roll with fixed successes and 10s counting twice: 7t2+2e");
        list.add("Roll with custom DC: 7t2 dc7");
        list.add("Roll with fixed successes, 10s counting twice and a custom DC: 7t2+2e dc7");
        assertEquals(list, details.getExamples());
    }
}
