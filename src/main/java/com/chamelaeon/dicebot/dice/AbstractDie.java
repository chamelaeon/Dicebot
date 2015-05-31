package com.chamelaeon.dicebot.dice;

import java.util.ArrayList;
import java.util.List;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;

/**
 * Abstract implementation of Die that handles rolling each die.
 * @author Chamelaeon
 *
 */
public abstract class AbstractDie implements Die {
	@Override
	public List<DieResult> rollDice(int number, Random random, Reroll reroll, Explosion explosion, Statistics statistics) {
		List<DieResult> results = new ArrayList<DieResult>();
		for (int i = 0; i < number; i++) {
			DieResult rolled = rollDie(random, reroll, explosion, statistics);
			results.add(rolled);
		}
		return results;
	}
	
	/**
	 * Rolls the die once, recursively handling rerolls and explosions.
	 * @param random The {@link Random} to use for generating numbers.
	 * @param reroll The reroll behaviors of the die.
	 * @param explosion The explosion behaviors of the die.
	 * @param statistics The statistics for roll tracking.
	 * @return the result of the die.
	 */
	public abstract DieResult rollDie(Random random, Reroll reroll, Explosion explosion, Statistics statistics);
}