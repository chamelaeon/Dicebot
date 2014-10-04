package com.chamelaeon.dicebot.dice;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.behavior.Behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;

/**
 * A die that can be used in a roll.
 * @author Chamelaeon
 */
public interface Die {

	/**
	 * Returns the number of sides on the die.
	 * @return the number of sides.
	 */
	public short getSides();
	
	/**
	 * Checks whether or not this die believes that the given value is a critical success, given the number
	 * of rolled dice.
	 * @param diceRolled The number of rolled dice.
	 * @param rolledValue The natural value of the roll.
	 * @return true if the roll is a critsuccess, false otherwise.
	 */
	public boolean isCritSuccess(long diceRolled, long rolledValue);
	
	/**
	 * Rolls the die, recursively handling rerolls and explosions. 
	 * @param random The {@link Random} to use for generating numbers.
	 * @param reroll The reroll behaviors of the dice.
	 * @param explosion The explosion behaviors of the dice.
	 * @param statistics The statistics for roll tracking.
	 * @return the value of the roll.
	 */
	public DieResult rollDie(Random random, Reroll reroll, Explosion explosion, Statistics statistics);
	
	/** Simple die implementation. */
	public static class SimpleDie implements Die {
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
		
		@Override
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
	
	/** Fudge dice implementation. */
	public static class FudgeDie implements Die {
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
}
