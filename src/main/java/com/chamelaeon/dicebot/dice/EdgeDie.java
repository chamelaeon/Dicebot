/**
 * 
 */
package com.chamelaeon.dicebot.dice;

import java.util.ArrayList;
import java.util.List;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;

/**
 * A die for Edge rolls in Shadowrun 4. Allows adding more of itself to the pool of dice.
 * @author Chamelaeon
 */
public class EdgeDie implements Die {

	@Override
	public short getSides() {
		return 6;
	}

	@Override
	public boolean isCritSuccess(long diceRolled, long rolledValue) {
		// No such animal in SR4.
		return false;
	}

	@Override
	public List<DieResult> rollDice(int number, Random random, Reroll reroll, Explosion explosion, Statistics statistics) {
		List<DieResult> results = new ArrayList<DieResult>();
		for (int i = 0; i < number; i++) {
			results.addAll(performRoll(random, statistics));
		}
		return results;
	}

	private List<DieResult> performRoll(Random random, Statistics statistics) {
		List<DieResult> results = new ArrayList<DieResult>();
		int roll = random.getRoll(6, statistics);
		results.add(new DieResult(roll, false));
		
		// If it's a 6, add another die to the pool, with potential to add more.
		if (roll == 6) {
			results.addAll(performRoll(random, statistics));
		}
		return results;
	}
}
