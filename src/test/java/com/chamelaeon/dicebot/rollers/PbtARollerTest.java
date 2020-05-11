package com.chamelaeon.dicebot.rollers;

import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.random.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class PbtARollerTest extends RollerTestBase {

	PbtARoller roller;

	@Before
	public void setUp() throws Exception {
		doPersonalitySetup();
		roller = new PbtARoller(personality);
	}

	@After
	public void tearDown() throws Exception {
		doPersonalityTeardown();
	}

	@Test
	public void testBasicRegexp() {
		String regexp = PbtARoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("dA");
		m.find();
		assertEquals("dA", m.group(0));
		assertNull(m.group(1));
		assertEquals("", m.group(2));
	}

	@Test
	public void testBasicRollMiss() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		roller = new PbtARoller(personality, random);

		// Ensure a miss.
		when(random.getRoll(6, statistics)).thenReturn(1);

		String[] parts = new String[] {"dA", null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).getRollResult(eq("PbtA1GroupMiss"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", "pbtaMiss"), tokenSubMatcher("%ROLLCOMMENT%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testBasicRollMissWithMessage() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		String rollComment = "it's a miss";
		roller = new PbtARoller(personality, random);

		// Ensure a miss.
		when(random.getRoll(6, statistics)).thenReturn(1);
		when(personality.shouldShowMessagesForRollResultType("pbtaMiss")).thenReturn(true);
		when(personality.chooseRollResultTypeCommentLine("pbtaMiss")).thenReturn(rollComment);

		String[] parts = new String[] {"dA", null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).shouldShowMessagesForRollResultType("pbtaMiss");
		verify(personality).chooseRollResultTypeCommentLine("pbtaMiss");
		verify(personality).getRollResult(eq("PbtA1GroupMissWithMessage"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", "pbtaMiss"), tokenSubMatcher("%ROLLCOMMENT%", rollComment),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testBasicRollHit() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		roller = new PbtARoller(personality, random);

		// Ensure a nit.
		when(random.getRoll(6, statistics)).thenReturn(4);

		String[] parts = new String[] {"dA", null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).getRollResult(eq("PbtA1GroupHit"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", "pbtaHit"), tokenSubMatcher("%ROLLCOMMENT%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testBasicRollHitWithMessage() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		String rollComment = "it's a hit";
		roller = new PbtARoller(personality, random);

		// Ensure a miss.
		when(random.getRoll(6, statistics)).thenReturn(4);
		when(personality.shouldShowMessagesForRollResultType("pbtaHit")).thenReturn(true);
		when(personality.chooseRollResultTypeCommentLine("pbtaHit")).thenReturn(rollComment);

		String[] parts = new String[] {"dA", null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).shouldShowMessagesForRollResultType("pbtaHit");
		verify(personality).chooseRollResultTypeCommentLine("pbtaHit");
		verify(personality).getRollResult(eq("PbtA1GroupHitWithMessage"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", "pbtaHit"), tokenSubMatcher("%ROLLCOMMENT%", rollComment),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testBasicRollStrongHit() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		roller = new PbtARoller(personality, random);

		// Ensure a nit.
		when(random.getRoll(6, statistics)).thenReturn(6);

		String[] parts = new String[] {"dA", null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).getRollResult(eq("PbtA1GroupStrongHit"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", "pbtaStrongHit"), tokenSubMatcher("%ROLLCOMMENT%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testBasicRollStrongHitWithMessage() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		String rollComment = "it's a hit";
		roller = new PbtARoller(personality, random);

		// Ensure a miss.
		when(random.getRoll(6, statistics)).thenReturn(6);
		when(personality.shouldShowMessagesForRollResultType("pbtaStrongHit")).thenReturn(true);
		when(personality.chooseRollResultTypeCommentLine("pbtaStrongHit")).thenReturn(rollComment);

		String[] parts = new String[] {"dA", null, null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).shouldShowMessagesForRollResultType("pbtaStrongHit");
		verify(personality).chooseRollResultTypeCommentLine("pbtaStrongHit");
		verify(personality).getRollResult(eq("PbtA1GroupStrongHitWithMessage"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", "pbtaStrongHit"), tokenSubMatcher("%ROLLCOMMENT%", rollComment),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testMultipleGroupsRegexp() {
		String regexp = PbtARoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("10 dA");
		m.find();
		assertEquals("10 dA", m.group(0));
		assertEquals("10 ", m.group(1));
		assertEquals("", m.group(2));
	}

	@Test
	public void testMultipleGroupsRoll() throws InputException {
		Statistics statistics = mock(Statistics.class);
		String[] parts = new String[] {"10 dA", "10 ", null};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseShort("10 ");
		verify(personality).getRollResult(eq("PbtAMoreGroups"), tokenSubMatcher("%GROUPCOUNT%", "10"),
				tokenSubMatcher("%MODIFIER%", ""), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", ""), tokenSubMatcher("%ROLLCOMMENT%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testRollWithModifierRegexp() {
		String regexp = PbtARoller.getRegexp();
		Pattern p = Pattern.compile(regexp);

		Matcher m = p.matcher("dA+5");
		m.find();
		assertEquals("dA+5", m.group(0));
		assertNull(m.group(1));
		assertEquals("+5", m.group(2));
	}

	@Test
	public void testRollWithModifier() throws InputException {
		Statistics statistics = mock(Statistics.class);
		Random random = mock(Random.class);
		roller = new PbtARoller(personality, random);

		// Ensure a nit.
		when(random.getRoll(6, statistics)).thenReturn(2);

		String[] parts = new String[] {"dA+5", null, "+5"};
		roller.assembleRoll(parts, testNick, statistics);

		verify(personality).parseShort("5");
		verify(personality).getRollResult(eq("PbtA1GroupHit"), tokenSubMatcher("%GROUPCOUNT%", "1"),
				tokenSubMatcher("%MODIFIER%", "+5"), tokenSubMatcher("%USER%", testNick),
				tokenSubMatcher("%ROLLTYPE%", "pbtaHit"), tokenSubMatcher("%ROLLCOMMENT%", ""),
				tokenSubMatcher("%NATURALVALUE%", "*"), tokenSubMatcher("%MODIFIEDVALUE%", "*"));
	}

	@Test
	public void testHelpDetails() {
		HelpDetails details = roller.getHelpDetails();
		assertEquals("Powered by the Apocalypse", details.getCommandName());
		assertEquals("A dice roller for games Powered by the Apocalypse (e.g. Apocalypse World, Dungeon World, Urban Shadows, and so on). " +
						"You roll using the format `dA`, which always rolls 2d6. Positive or negative modifiers may be applied to affect the result " +
						"(ex. dA-1). To roll additional groups of die, prefix the roll with a number then a space (ex. 10 dA+3). Modifiers will be " +
						"applied to each group individually. The result will indicate a miss, a hit, or a strong hit where appropriate. Since PbtA " +
						"games vary widely, it won't handle hold or anything specific to a move.",
				details.getDescription());
		assertEquals("roller", details.getType());

		List<String> list = new ArrayList<>();
		list.add("Normal rolls: dA, which will roll 2d6");
		list.add("Roll with modifier: dA+3");
		assertEquals(list, details.getExamples());
	}
}
