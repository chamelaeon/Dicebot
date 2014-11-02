package com.chamelaeon.dicebot.dice;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;

/** Simple die implementation. */
public class SimpleDie extends AbstractDie {
	/** The number of sides on the die. */
	private final short sides;

	/**
	 * Constructor.
	 * @param sides The number of sides.
	 */
	public SimpleDie(short sides) {
		this.sides = sides;
	}
	
	@Override
	public short getSides() {
		return sides;
	}
	
	@Override
	public boolean isCritSuccess(long diceRolled, long rolledValue) {
		return rolledValue == diceRolled * sides;
	}
	
	public DieResult rollDie(Random random, Reroll reroll, Explosion explosion, Statistics statistics) {
		int roll = random.getRoll(sides, statistics);
		boolean wasRerolled = false;
		
		// Check for reroll.
		if (null != reroll && reroll.needsRerolled(roll)) {
			roll = random.getRoll(sides, statistics);
			while (reroll.needsRerolled(roll) && reroll.forceGoodValue()) {
				roll = random.getRoll(sides, statistics);
			}
			wasRerolled = true;
		}
		
		// Check for explosion.
		if (null != explosion && explosion.shouldExplode(roll)) {
			int nextDie = random.getRoll(sides, statistics);
			roll += nextDie;
			while (explosion.shouldExplode(nextDie)) {
				nextDie = random.getRoll(sides, statistics);
				roll += nextDie;
			}
		}
	
		return new DieResult(roll, wasRerolled);
	}
}