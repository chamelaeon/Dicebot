package com.chamelaeon.dicebot.rollers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

public class L5RRollerTest extends RollerTestBase {
	
	L5RRoller roller;
	
    @Before
    public void setUp() throws Exception {
        doPersonalitySetup();
        roller = new L5RRoller(personality);
    }

    @After
    public void tearDown() throws Exception {
    	doPersonalityTeardown();
    }
    
    @Test
    public void testBasicRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5");
        m.find();
        assertEquals("9k5", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertNull(m.group(4));
        assertEquals("", m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testBasicRoll() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5", null, "9", "5", null, null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testBasicRollWithRollover() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"14k6", null, "14", "6", null, null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("14");        
        verify(personality).parseShort("6");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "10"), tokenSubMatcher("%KEPTDICE%", "8"),
                tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testBasicRollWithRolloverPast10K10() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"14k10", null, "14", "10", null, null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("14");        
        verify(personality).parseShort("10");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "10"), tokenSubMatcher("%KEPTDICE%", "10"),
                tokenSubMatcher("%MODIFIER%", "+8"), tokenSubMatcher("%BEHAVIORS%", ""),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testBasicRollWithRolloverAt11k10() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"11k8", null, "11", "8", null, null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("11");        
        verify(personality).parseShort("8");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "10"), tokenSubMatcher("%KEPTDICE%", "8"),
                tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testBasicRollWithDoubleOverflowRollover() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"14k9", null, "14", "9", null, null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("14");        
        verify(personality).parseShort("9");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "10"), tokenSubMatcher("%KEPTDICE%", "10"),
                tokenSubMatcher("%MODIFIER%", "+4"), tokenSubMatcher("%BEHAVIORS%", ""),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test(expected = InputException.class)
    public void testRollWithMoreKeptThanRolled() throws InputException {
    	try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"8k10", null, "8", "10", null, null, null, null, null};
			when(personality.getException("RollLessThanKeep")).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseShort("10");
			verify(personality).parseShort("8");
			verify(personality).getException("RollLessThanKeep");
		}
    }
    
    @Test(expected = InputException.class)
	public void testRollWithNoKeptDice() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"10k0", null, "10", "0", null, null, null, null, null};
			when(personality.getException("KeepingLessThan1")).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseShort("10");
			verify(personality).parseShort("0");
			verify(personality).getException("KeepingLessThan1");
		}
	}
    
    @Test
    public void testMultipleGroupsRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("10 9k5");
        m.find();
        assertEquals("10 9k5", m.group(0));
        assertEquals("10 ", m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertNull(m.group(4));
        assertEquals("", m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testMultipleGroupsRoll() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"10 9k5", "10 ", "9", "5", null, null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).parseShort("10 ");
        verify(personality).getRollResult(eq("L5RMoreGroups"), tokenSubMatcher("%GROUPCOUNT%", "10"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithModifierRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5+7");
        m.find();
        assertEquals("9k5+7", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertNull(m.group(4));
        assertEquals("+7", m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testRollWithModifier() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5+7", null, "9", "5", null, "+7", null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).parseShort("7");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", "+7"), tokenSubMatcher("%BEHAVIORS%", ""),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }

    @Test
    public void testRollWithEmphasisRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5e");
        m.find();
        assertEquals("9k5e", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertEquals("e", m.group(4));
        assertEquals("", m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testRollWithEmphasis() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5e", null, "9", "5", "e", null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", "e"),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithModifierAndEmphasisRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5+7e");
        m.find();
        assertEquals("9k5+7e", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertNull(m.group(4));
        assertEquals("+7", m.group(5));
        assertEquals("e", m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testRollWithModifierAndEmphasis() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5+7e", null, "9", "5", null, "+7", "e", null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).parseShort("7");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", "+7"), tokenSubMatcher("%BEHAVIORS%", "e"),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithTwoModifiersRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5e+7m");
        m.find();
        assertEquals("9k5e+7m", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertEquals("e", m.group(4));
        assertEquals("+7", m.group(5));
        assertEquals("m", m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testRollWithTwoModifiers() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5+7e", null, "9", "5", "e", "+7", "m", null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).parseShort("7");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", "+7"), tokenSubMatcher("%BEHAVIORS%", "em"),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithMasteryRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5m");
        m.find();
        assertEquals("9k5m", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertEquals("m", m.group(4));
        assertEquals("", m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testRollWithMastery() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5m", null, "9", "5", "m", null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", "m"),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithRawRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5r");
        m.find();
        assertEquals("9k5r", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertEquals("r", m.group(4));
        assertEquals("", m.group(5));
        assertNull(m.group(6));
        assertNull(m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testRollWithRaw() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5r", null, "9", "5", "r", null, null, null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", "r"),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithEverythingRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5+7em and a partridge in a pear tree");
        m.find();
        assertEquals("9k5+7em and a partridge in a pear tree", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertNull(m.group(4));
        assertEquals("+7", m.group(5));
        assertEquals("em", m.group(6));
        assertNull(m.group(7));
        assertEquals(" and a partridge in a pear tree", m.group(8));
    }
    
    @Test
    public void testRollWithEverything() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5+7em", null, "9", "5", null, "+7", "em", null, " and a partridge in a pear tree"};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).parseShort("7");
        verify(personality).getRollResult(eq("L5ROneGroup"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%ROLLEDDICE%", "9"), tokenSubMatcher("%KEPTDICE%", "5"),
                tokenSubMatcher("%MODIFIER%", "+7"), tokenSubMatcher("%BEHAVIORS%", "em"),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%NATURALVALUE%", "*"), 
                tokenSubMatcher("%MODIFIEDVALUE%", "*"), tokenSubMatcher("%ANNOTATION%", " [and a partridge in a pear tree]"));
    }
    
    @Test
    public void testAnalyzeRollRegexp() {
        String regexp = L5RRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("9k5 a");
        m.find();
        assertEquals("9k5 a", m.group(0));
        assertNull(m.group(1));
        assertEquals("9", m.group(2));
        assertEquals("5", m.group(3));
        assertNull(m.group(4));
        assertEquals("", m.group(5));
        assertNull(m.group(6));
        assertEquals(" a", m.group(7));
        assertNull(m.group(8));
    }
    
    @Test
    public void testAnalyzeRoll() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"9k5+7em a", null, "9", "5", null, "+7", "em", "a", null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("9");        
        verify(personality).parseShort("5");
        verify(personality).parseShort("7");
        verify(personality).getMessage(eq("L5RAnalyze"), tokenSubMatcher("%ROLLEDDICE%", "9"), 
                tokenSubMatcher("%KEPTDICE%", "5"), tokenSubMatcher("%MODIFIER%", "+7"), tokenSubMatcher("%BEHAVIORS%", "em"),
                tokenSubMatcher("%USER%", testNick),  tokenSubMatcher("%AVERAGE%", "*"));
    }
    
    @Test
    public void testHelpDetails() {
        HelpDetails details = roller.getHelpDetails();
        assertEquals("L5R", details.getCommandName());
        assertEquals("A dice roller for Legend of the Five Rings (roll/keep style), which rolls X number of d10s and "
        		+ "keeps Y of them (ex. 5k3). Positive or negative modifiers may be applied to affect the result "
        		+ "(ex. 5k3-5). Rolls that would \"roll over\" into static bonuses are automatically converted (ex. "
				+ "13k9 into 10k10+2). To roll additional groups of die, prefix the roll with a number then a space "
				+ "(ex. 10 2k2-5). Modifiers will be applied to each group individually. Emphasis rolls are available "
				+ "by appending \"e\" to the roll (ex. 9k5e). Mastery is also available by appending \"m\" (ex. 12k3m). "
				+ "They may be combined (ex. 12k3+5em). To analyze the predicted average of a given roll, put an 'a' after " +
				"the roll, separated by a space (ex. 9k5e+2 a). This can help with knowing how many raises you should take.", 
        		details.getDescription());
        assertEquals("roller", details.getType());

        List<String> list = new ArrayList<>();
        list.add("Basic roll: 5k2");
        list.add("Roll with modifier: 7k3+5");
        list.add("Roll with rollover: 14k6");
        list.add("Roll with emphasis: 9k2e");
        list.add("Roll with modifier and emphasis: 10k6+16e");
        list.add("Roll with mastery: 9k3m");
        list.add("Roll with modifier, emphasis, and mastery: 10k6me");
        list.add("Roll with no explosions (raw): 9k2r");
        list.add("Roll with no explosions (raw) and emphasis: 9k2r"); 
        list.add("Roll with analysis: 9k2me+3 a");
        list.add("One with everything: 10k8+16me");
        assertEquals(list, details.getExamples());
    }
}
