package com.chamelaeon.dicebot.dice;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;

/** Fudge dice implementation. */
public class FudgeDie extends AbstractDie {
	@Override
	public short getSides() {
		return 6;
	}

	@Override
	public boolean isCritSuccess(long diceRolled, long rolledValue) {
		// No such animal in Fudge.
		return false;
	}

	@Override
	public DieResult rollDie(Random random, Reroll reroll, Explosion explosion, Statistics statistics) {
		int roll = random.getRoll(6, statistics);
		
		if (roll < 3) {
			return new DieResult(-1, false);
		} else if (roll < 5) {
			return new DieResult(0, false);
		} else {
			return new DieResult(1, false);
		}
	}
}