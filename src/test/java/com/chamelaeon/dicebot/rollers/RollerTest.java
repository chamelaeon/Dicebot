package com.chamelaeon.dicebot.rollers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.chamelaeon.dicebot.random.Random;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.User;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;

public class RollerTest extends RollerTestBase {

	Roller roller;
	Personality personality;
	String testMessage = "test message";
	String testNick = "testNick";

	@Before
	public void startUp() throws Exception {
		personality = mock(Personality.class);
		roller = new Roller("", "test roller", "test roller", new ArrayList<String>(), personality) {
			@Override
			protected String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
				return testMessage;
			}
		};
	}

	@Test
	public void testOnSuccess() throws InputException {
		@SuppressWarnings("unchecked")
		DicebotGenericEvent<Dicebot> event = mock(DicebotGenericEvent.class);
		User user = mock(User.class);
		Statistics statistics = mock(Statistics.class);
		Dicebot bot = mock(Dicebot.class);
		List<String> groups = Collections.emptyList();

		when(event.getUser()).thenReturn(user);
		when(user.getNick()).thenReturn(testNick);
		when(event.getBot()).thenReturn(bot);
		when(bot.getStatistics()).thenReturn(statistics);

		roller.onSuccess(event, groups);

		verify(event).respondWithAction(testMessage);
	}

	@Test
	public void testGetPersonality() {
		assertSame(personality, roller.getPersonality());
	}

	@Test
	public void testParseGroups() throws InputException {
		when(personality.parseShort(anyString())).thenAnswer(new ParsingAnswer());

		assertEquals((short) 1, roller.parseGroups("1"));
	}

	@Test
	public void testParseGroupsOver10() throws InputException {
		when(personality.parseShort(anyString())).thenAnswer(new ParsingAnswer());

		assertEquals((short) 10, roller.parseGroups("15"));
	}

	@Test
	public void testParseNullGroups() throws InputException {
		when(personality.parseShort(anyString())).thenAnswer(new ParsingAnswer());

		assertEquals((short) 1, roller.parseGroups(null));
	}

	@Test(expected = InputException.class)
	public void testParseLessThanOneGroups() throws InputException {
		when(personality.getException("LessThanOneGroup")).thenReturn(new InputException(""));
		roller.parseGroups("0");
	}

	@Test
	public void testBuildModifiedList() {
		GroupResult group = mock(GroupResult.class);
		when(group.getModified()).thenReturn(1L, 2L, 3L, 4L, 5L);

		assertEquals("1 2 3 4 5 ", roller.buildModifiedList(Arrays.asList(group, group, group, group, group)));
	}

	@Test
	public void testBuildNaturalList() {
		GroupResult group = mock(GroupResult.class);
		when(group.getNatural()).thenReturn(1L, 2L, 3L, 4L, 5L);

		assertEquals("1 2 3 4 5 ", roller.buildNaturalList(Arrays.asList(group, group, group, group, group)));
	}

	@Test
	public void testGetAnnotationString() {
		String partString = "partstr";
		assertEquals(" [partstr]", roller.getAnnotationString(partString));
	}

	@Test
	public void testGetAnnotationStringNull() {
		assertEquals("", roller.getAnnotationString(null));
	}

	@Test
	public void testGetAnnotationStringEmpty() {
		assertEquals("", roller.getAnnotationString(""));
	}

	@Test
	public void testGetAnnotationStringEmptyAfterTrim() {
		assertEquals("", roller.getAnnotationString("   "));
	}

	//
//	@Test
//	public void testPerformRollCritSuccess() {
//		Statistics statistics = mock(Statistics.class);
//		Random random = mock(Random.class);
//
//		when(die.rollDice(rolled, random, reroll, explosion, statistics)).thenReturn(getResultList(rolled, 8));
//		when(die.isCritSuccess(5, 40)).thenReturn(true);
//		when(personality.useCritSuccesses()).thenReturn(true);
//
//		List<GroupResult> result = roll.performRoll(1, random, statistics);
//		assertEquals(40, result.get(0).getNatural());
//		assertTrue(result.get(0).isCriticalSuccess());
//		assertFalse(result.get(0).isCriticalFailure());
//
//		verify(die).rollDice(rolled, random, reroll, explosion, statistics);
//		verify(die).isCritSuccess(5, 40);
//		verify(modifier).apply(40);
//		verify(personality).useCritSuccesses();
//	}
//
//	@Test
//	public void testPerformRollCritFail() {
//		Statistics statistics = mock(Statistics.class);
//		Random random = mock(Random.class);
//
//		when(die.rollDice(rolled, random, reroll, explosion, statistics)).thenReturn(getResultList(rolled, 1));
//		when(personality.useCritFailures()).thenReturn(true);
//
//		List<GroupResult> result = roll.performRoll(1, random, statistics);
//		assertEquals(5, result.get(0).getNatural());
//		assertFalse(result.get(0).isCriticalSuccess());
//		assertTrue(result.get(0).isCriticalFailure());
//
//		verify(die).rollDice(rolled, random, reroll, explosion, statistics);
//		verify(modifier).apply(5);
//		verify(personality).useCritFailures();
//	}
}
