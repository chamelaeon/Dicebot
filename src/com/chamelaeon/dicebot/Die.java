package com.chamelaeon.dicebot;

import com.chamelaeon.dicebot.Behavior.Explosion;
import com.chamelaeon.dicebot.Behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;

/**
 *
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
	public int rollDie(Random random, Reroll reroll, Explosion explosion, Statistics statistics);
	
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
		public int rollDie(Random random, Reroll reroll, Explosion explosion, Statistics statistics) {
			int roll = random.getRoll(sides);
			
			// Check for reroll.
			if (null != reroll && reroll.needsRerolled(roll)) {
				roll = random.getRoll(sides);
				while (reroll.needsRerolled(roll) && reroll.forceGoodValue()) {
					roll = random.getRoll(sides);
				}
			}
			
			// Check for explosion.
			if (null != explosion && explosion.shouldExplode(roll)) {
				int nextDie = random.getRoll(sides);
				roll += nextDie;
				while (explosion.shouldExplode(nextDie)) {
					nextDie = random.getRoll(sides);
					roll += nextDie;
				}
			}
		
			return roll;
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
		public int rollDie(Random random, Reroll reroll, Explosion explosion, Statistics statistics) {
			int roll = random.getRoll(6);
			
			if (roll < 3) {
				return -1;
			} else if (roll < 5) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
