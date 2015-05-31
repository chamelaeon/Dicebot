package com.chamelaeon.dicebot.random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.chamelaeon.dicebot.api.Statistics;
/*
 * Don't worry too hard about this one, it just wraps the Java Random...
 */
public class BasicRandomTest {

	Random random = new BasicRandom();
	

	@Test
	public void testGetRollInt() {
		for (int i = 0; i < 100; i++) {
			int roll = random.getRoll(100);
			assertTrue(roll >= 1);
			assertTrue(roll <= 100);
		}
	}

	@Test
	public void testGetRollIntStatistics() {
		Statistics statistics = mock(Statistics.class);
		
		for (int i = 0; i < 100; i++) {
			int roll = random.getRoll(100, statistics);
			assertTrue(roll >= 1);
			assertTrue(roll <= 100);
			verify(statistics, atLeastOnce()).registerRoll(100, roll);
		}
	}

}
