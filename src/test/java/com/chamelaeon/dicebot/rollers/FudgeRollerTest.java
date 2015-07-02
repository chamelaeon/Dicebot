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

public class FudgeRollerTest extends RollerTestBase {

    FudgeRoller roller;
	
    @Before
    public void setUp() throws Exception {
        doPersonalitySetup();
        roller = new FudgeRoller(personality);
    }

    @After
    public void tearDown() throws Exception {
    	doPersonalityTeardown();
    }
    
    @Test
    public void testBasicRegexp() {
        String regexp = FudgeRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("4dF");
        m.find();
        assertEquals("4dF", m.group(0));
        assertNull(m.group(1));
        assertEquals("4", m.group(2));
        assertEquals("", m.group(3));
        assertNull(m.group(4));
    }
    
    @Test
    public void testBasicRoll() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"4dF", null, "4", null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseDiceCount("4");
        verify(personality).getRollResult(eq("Fudge1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%DICECOUNT%", "4"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%FUDGEVALUE%", "*"),
                tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"), 
                tokenSubMatcher("%DESCRIPTOR%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollOffTopOfScale() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"4dF", null, "4", "+20", null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseDiceCount("4");
        verify(personality).parseShort("20");
        verify(personality).getRollResult(eq("Fudge1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%DICECOUNT%", "4"), tokenSubMatcher("%MODIFIER%", "+20"), 
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%FUDGEVALUE%", "*"),
                tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"), 
                tokenSubMatcher("%DESCRIPTOR%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollOffBottomOfScale() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"4dF", null, "4", "-20", null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseDiceCount("4");
        verify(personality).parseShort("20");
        verify(personality).getRollResult(eq("Fudge1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%DICECOUNT%", "4"), tokenSubMatcher("%MODIFIER%", "-20"), 
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%FUDGEVALUE%", "*"),
                tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"), 
                tokenSubMatcher("%DESCRIPTOR%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test(expected = InputException.class)
	public void testRollWithNoDice() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"0dF", null, "0", null, null, null, null};
			when(personality.getException("Roll0Dice")).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseDiceCount("0");
			verify(personality).getException("Roll0Dice");
		}
	}
    
    @Test
    public void testMultipleGroupsRegexp() {
        String regexp = FudgeRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("10 4dF");
        m.find();
        assertEquals("10 4dF", m.group(0));
        assertEquals("10 ", m.group(1));
        assertEquals("4", m.group(2));
        assertEquals("", m.group(3));
        assertNull(m.group(4));
    }
    
    @Test
    public void testMultipleGroupsRoll() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"10 4dF", "10 ", "4", null, null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseShort("10 ");
        verify(personality).parseDiceCount("4");
        verify(personality).getRollResult(eq("FudgeMoreGroups"), tokenSubMatcher("%GROUPCOUNT%", "10"),
                tokenSubMatcher("%DICECOUNT%", "4"), tokenSubMatcher("%MODIFIER%", ""), 
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%FUDGEVALUE%", "*"),
                tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"), 
                tokenSubMatcher("%DESCRIPTOR%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithModifierRegexp() {
        String regexp = FudgeRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("4dF+5");
        m.find();
        assertEquals("4dF+5", m.group(0));
        assertNull(m.group(1));
        assertEquals("4", m.group(2));
        assertEquals("+5", m.group(3));
        assertNull(m.group(4));
    }
    
    @Test
    public void testRollWithModifier() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"4dF+5", null, "4", "+5", null};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseDiceCount("4");
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("Fudge1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%DICECOUNT%", "4"), tokenSubMatcher("%MODIFIER%", "+5"), 
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%FUDGEVALUE%", "*"),
                tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"), 
                tokenSubMatcher("%DESCRIPTOR%", "*"), tokenSubMatcher("%ANNOTATION%", ""));
    }
    
    @Test
    public void testRollWithAnnotationRegexp() {
        String regexp = FudgeRoller.getRegexp();
        Pattern p = Pattern.compile(regexp);
        
        Matcher m = p.matcher("4dF+5 and a goat");
        m.find();
        assertEquals("4dF+5 and a goat", m.group(0));
        assertNull(m.group(1));
        assertEquals("4", m.group(2));
        assertEquals("+5", m.group(3));
        assertEquals(" and a goat", m.group(4));
    }
    
    @Test
    public void testRollWithAnnotation() throws InputException {
        Statistics statistics = mock(Statistics.class);
        String[] parts = new String[] {"4dF+5 and a goat", null, "4", "+5", " and a goat"};
        roller.assembleRoll(parts, testNick, statistics);
        
        verify(personality).parseDiceCount("4");
        verify(personality).parseShort("5");
        verify(personality).getRollResult(eq("Fudge1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
                tokenSubMatcher("%DICECOUNT%", "4"), tokenSubMatcher("%MODIFIER%", "+5"), 
                tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%FUDGEVALUE%", "*"),
                tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"), 
                tokenSubMatcher("%DESCRIPTOR%", "*"), tokenSubMatcher("%ANNOTATION%", " [and a goat]"));
    }
    
    @Test
    public void testHelpDetails() {
        HelpDetails details = roller.getHelpDetails();
        assertEquals("Fudge", details.getCommandName());
        assertEquals("A dice roller for the FUDGE dice style, which rolls X number of d6s with faces of "
        		+ "['-', '-', ' ', ' ', '+', '+'] and returns the additive result (ex. 4dF).", 
        		details.getDescription());
        assertEquals("roller", details.getType());

        List<String> list = new ArrayList<>();
        list.add("Basic roll: 4dF");
        list.add("Roll with modifier: 4dF+3");
        assertEquals(list, details.getExamples());
    }
}
