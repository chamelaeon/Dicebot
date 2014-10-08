package com.chamelaeon.dicebot.rollers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
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

public class StandardRollerTest extends RollerTestBase {

	StandardRoller roller;

	@Before
	public void setUp() throws Exception {
		doPersonalitySetup();
		roller = new StandardRoller(personality);
	}

	@After
	public void tearDown() throws Exception {
		doPersonalityTeardown();
	}

	@Test
	public void testSuperBasicRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("d6");
		m.find();
		assertEquals("d6", m.group(0));
		assertNull(m.group(1));
		assertEquals("", m.group(2));
		assertEquals("6", m.group(3));
		assertNull(m.group(4));
		assertNull(m.group(5));
		assertNull(m.group(6));
	}

	@Test
	public void testSuperBasicRoll() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"d6", null, "", "6", null, null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("");
		verify(personality).parseShort("6");
		verify(personality).getRollResult(eq("Standard1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "1"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	
	@Test
	public void testBasicRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("2d6");
		m.find();
		assertEquals("2d6", m.group(0));
		assertNull(m.group(1));
		assertEquals("2", m.group(2));
		assertEquals("6", m.group(3));
		assertNull(m.group(4));
		assertNull(m.group(5));
		assertNull(m.group(6));
	}

	@Test
	public void testBasicRoll() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"2d6", null, "2", "6", null, null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("2");
		verify(personality).parseShort("6");
		verify(personality).getRollResult(eq("Standard1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "2"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test(expected = InputException.class)
	public void testRollWithNoDice() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"0d6", null, "0", "6", null, null, null};
			when(personality.getException("Roll0Dice")).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseDiceCount("0");
			verify(personality).parseShort("6");
			verify(personality).getException("Roll0Dice");
		}
	}

	@Test(expected = InputException.class)
	public void testRollWithNoSides() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"1d0", null, "1", "0", null, null, null};
			when(personality.getException("Roll0Sides")).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseDiceCount("1");
			verify(personality).parseShort("0");
			verify(personality).getException("Roll0Sides");
		}
	}

	@Test(expected = InputException.class)
	public void testRollWithOneSidedDice() throws InputException {
		try {
			Statistics statistics = mock(Statistics.class);
			String[] parts = new String[] {"1d1", null, "1", "1", null, null, null};
			when(personality.getException(eq("OneSidedDice"), (TokenSubstitution[]) notNull())).thenReturn(new InputException("error"));
			roller.assembleRoll(parts, testNick, statistics);
		} finally {
			verify(personality).parseDiceCount("1");
			verify(personality).parseShort("1");
			verify(personality).getException(eq("OneSidedDice"), tokenSubMatcher("%DICECOUNT%", "1"));
		}
	}

	@Test
	public void testMultipleGroupsRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("10 2d6");
		m.find();
		assertEquals("10 2d6", m.group(0));
		assertEquals("10 ", m.group(1));
		assertEquals("2", m.group(2));
		assertEquals("6", m.group(3));
		assertNull(m.group(4));
		assertNull(m.group(5));
		assertNull(m.group(6));
	}

	@Test
	public void testRollWithCriticalFailure() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		String critFailLine = "critical failure";
		roller = new StandardRoller(personality, random);

		// Ensure a critical failure. 
		when(random.getRoll(2, statistics)).thenReturn(1);
		when(personality.useCritFailures()).thenReturn(true);
		when(personality.chooseCriticalFailureLine()).thenReturn(critFailLine);

		String[] parts = new String[] {"1d2", null, "1", "2", null, null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("1");
		verify(personality).parseShort("2");
		verify(personality).useCritFailures();
		verify(personality).chooseCriticalFailureLine();
		verify(personality).getRollResult(eq("Standard1GroupCrit"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "1"), tokenSubMatcher("%DICETYPE%", "2"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", "FAILURE"),
				tokenSubMatcher("%CRITICALCOMMENT%", critFailLine), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testRollWithCriticalSuccess() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String critSuccLine = "critical success";
		when(personality.useCritSuccesses()).thenReturn(true);
		when(personality.chooseCriticalSuccessLine()).thenReturn(critSuccLine);

		String[] parts = new String[] {"1d2", null, "1", "2", "b1", null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("1");
		verify(personality).parseShort("2");
		verify(personality).useCritSuccesses();
		verify(personality).chooseCriticalSuccessLine();
		verify(personality).getRollResult(eq("Standard1GroupCrit"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "1"), tokenSubMatcher("%DICETYPE%", "2"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", "b1"), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", "SUCCESS"),
				tokenSubMatcher("%CRITICALCOMMENT%", critSuccLine), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testMultipleGroupsRoll() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"10 2d6", "10 ", "2", "6", null, null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("2");
		verify(personality).parseShort("6");
		verify(personality).parseShort("10 ");
		verify(personality).getRollResult(eq("StandardMoreGroups"), tokenSubMatcher("%GROUPCOUNT%", "10"),
				tokenSubMatcher("%DICECOUNT%", "2"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%BEHAVIORS%", ""), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testRollWithModifierRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("2d6+5");
		m.find();
		assertEquals("2d6+5", m.group(0));
		assertNull(m.group(1));
		assertEquals("2", m.group(2));
		assertEquals("6", m.group(3));
		assertNull(m.group(4));
		assertEquals("+5", m.group(5));
		assertNull(m.group(6));
	}

	@Test
	public void testRollWithModifier() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"2d6+5", null, "2", "6", null, "+5", null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("2");
		verify(personality).parseShort("6");
		verify(personality).parseShort("5");
		verify(personality).getRollResult(eq("Standard1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "2"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", "+5"), tokenSubMatcher("%BEHAVIORS%", ""), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testRollWithBrutalRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("2d6b2+5");
		m.find();
		assertEquals("2d6b2+5", m.group(0));
		assertNull(m.group(1));
		assertEquals("2", m.group(2));
		assertEquals("6", m.group(3));
		assertEquals("b2", m.group(4));
		assertEquals("+5", m.group(5));
		assertNull(m.group(6));
	}

	@Test
	public void testRollWithBrutal() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"2d6b2+5", null, "2", "6", "b2", "+5", null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("2");
		verify(personality).parseShort("6");
		verify(personality).parseShort("5");
		verify(personality).getRollResult(eq("Standard1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "2"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", "+5"), tokenSubMatcher("%BEHAVIORS%", "b2"), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testRollWithVorpalRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("2d6v+5");
		m.find();
		assertEquals("2d6v+5", m.group(0));
		assertNull(m.group(1));
		assertEquals("2", m.group(2));
		assertEquals("6", m.group(3));
		assertEquals("v", m.group(4));
		assertEquals("+5", m.group(5));
		assertNull(m.group(6));
	}

	@Test
	public void testRollWithVorpal() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"2d6v+5", null, "2", "6", "v", "+5", null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("2");
		verify(personality).parseShort("6");
		verify(personality).parseShort("5");
		verify(personality).getRollResult(eq("Standard1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "2"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", "+5"), tokenSubMatcher("%BEHAVIORS%", "v6"), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testRollWithNonMaxVorpalRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("2d6v5+5");
		m.find();
		assertEquals("2d6v5+5", m.group(0));
		assertNull(m.group(1));
		assertEquals("2", m.group(2));
		assertEquals("6", m.group(3));
		assertEquals("v5", m.group(4));
		assertEquals("+5", m.group(5));
		assertNull(m.group(6));
	}

	@Test
	public void testRollWithNonMaxVorpal() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"2d6v5+5", null, "2", "6", "v5", "+5", null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("2");
		verify(personality).parseShort("6");
		verify(personality).parseShort("5");
		verify(personality).getRollResult(eq("Standard1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "2"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", "+5"), tokenSubMatcher("%BEHAVIORS%", "v5"), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testRollWithAnnotationRegexp() {
		String regexp = StandardRoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("2d6v5+5 and a goat");
		m.find();
		assertEquals("2d6v5+5 and a goat", m.group(0));
		assertNull(m.group(1));
		assertEquals("2", m.group(2));
		assertEquals("6", m.group(3));
		assertEquals("v5", m.group(4));
		assertEquals("+5", m.group(5));
		assertEquals(" and a goat", m.group(6));
	}

	@Test
	public void testRollWithAnnotation() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"2d6v5+5 and a goat", null, "2", "6", "v5", "+5", " and a goat"};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseDiceCount("2");
		verify(personality).parseShort("6");
		verify(personality).parseShort("5");
		verify(personality).getRollResult(eq("Standard1Group"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%DICECOUNT%", "2"), tokenSubMatcher("%DICETYPE%", "6"),
				tokenSubMatcher("%MODIFIER%", "+5"), tokenSubMatcher("%BEHAVIORS%", "v5"), 
				tokenSubMatcher("%USER%", testNick), tokenSubMatcher("%CRITICALTYPE%", null),
				tokenSubMatcher("%CRITICALCOMMENT%", null), tokenSubMatcher("%ANNOTATION%", " [and a goat]"),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testHelpDetails() {
		HelpDetails details = roller.getHelpDetails();
		assertEquals("Standard", details.getCommandName());
		assertEquals("A standard dice roller, that can handle X number of dice of Y sides each, in the format XdY. (ex. 2d6). " +
				"Positive or negative modifiers may be applied to affect the result (ex. 2d6-5). If rolling only one die, the " +
				"initial number may be omitted (ex. d20+10). To roll additional groups of die, prefix the roll with a number " +
				"then a space (ex. 10 d20+10). Modifiers will be applied to each group individually. Brutal values of 1-9 are " +
				"available by adding \"b\" then a number (ex. 2d8b2+5). Vorpal is also available by appending \"v\" (ex. 2d8v+5).", 
				details.getDescription());
		assertEquals("roller", details.getType());

		List<String> list = new ArrayList<>();
		list.add("Normal rolls: 2d6, d20");
		list.add("Roll with modifier: d20+3");
		list.add("Roll with Brutal 2: 2d6b2");
		list.add("Roll with basic vorpal: d10v");
		list.add("Roll with expanded vorpal (5-10 range): d10v6");
		list.add("Roll with brutal 2 and modifier: 2d6b2+5");
		list.add("Roll with vorpal and modifier: 2d6v+3");
		assertEquals(list, details.getExamples());
	}
}
