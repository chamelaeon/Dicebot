package com.chamelaeon.dicebot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.chamelaeon.dicebot.Behavior.Explosion;
import com.chamelaeon.dicebot.Behavior.Reroll;
import com.chamelaeon.dicebot.personality.Personality;
import com.chamelaeon.dicebot.random.Random;


/** Class that represents a roll of dice. */
public class Roll {
	/** The number of rolled dice. */
	private final short rolled;
	/** The number of kept dice . */
	private final short kept;
	/** The number of sides on the dice to be rolled. */
	private final short sides;
	/** The modifier for the roll. */
	private final Modifier modifier;
	/** The reroll criteria, if any. */
	private final Reroll reroll;
	/** The explosion criteria, if any. */
	private final Explosion explosion;
	/** The Personality object for providing exception texts. */
	private Personality personality;
	
	/**
	 * Constructs a new roll.
	 * @param rolled The number of rolled dice.
	 * @param kept The number of kept dice.
	 * @param sides The number of sides on the dice to be rolled.
	 * @param modifier The numerical modifier for the roll.
	 * @throws InputException if there is an issue with the construction of the roll.
	 */
	public Roll(short rolled, short kept, short sides, Modifier modifier, Reroll reroll, Explosion explosion, Personality personality) 
	throws InputException {
		this.rolled = rolled;
		this.kept = kept;
		this.sides = sides;
		this.modifier = modifier;
		this.explosion = explosion;
		this.reroll = reroll;
		this.personality = personality;
		
		if (reroll != null && reroll.cannotBeSatisfied(sides)) {
			if (rolled == 1) {
				throw personality.getException("CannotSatisfyRerollSingleDie", reroll, sides);	
			} else {
				throw personality.getException("CannotSatisfyRerollMultipleDice", reroll, rolled, sides);
			}
		}
		
		if (explosion != null && explosion.explodesInfinitely(1)) {
			throw personality.getException("InfiniteExplosion");
		}
	}
	
	@Override
	public String toString() {
		return "Roll [rolled=" + rolled + ", kept=" + kept + ", sides=" + sides
				+ ", "
				+ (modifier != null ? "modifier=" + modifier + ", " : "")
				+ (reroll != null ? "reroll=" + reroll + ", " : "")
				+ (explosion != null ? "explosion=" + explosion + ", " : "")
				+ (personality != null ? "personality=" + personality : "")
				+ "]";
	}

	/**
	 * Makes a copy of the roll, with a few values altered, and returns it. Generally used for handling 
	 * dice "rollover" cases (like L5R).  
	 * @return the copied and altered roll.
	 * @throws InputException if the new roll cannot be constructed correctly.
	 */
	public Roll alterValues(int rolled, int kept, Modifier modifier) throws InputException {
		return alterValues((short) rolled, (short) kept, modifier);
	}
	
	/**
	 * Makes a copy of the roll, with a few values altered, and returns it. Generally used for handling 
	 * dice "rollover" cases (like L5R).  
	 * @return the copied and altered roll.
	 * @throws InputException if the new roll cannot be constructed correctly. 
	 */
	public Roll alterValues(short rolled, short kept, Modifier modifier) throws InputException {
		return new Roll(rolled, kept, this.sides, modifier, this.reroll, this.explosion, this.personality);
	}
	
	/**
	 * Gets the number of rolled dice.
	 * @return the rolled count.
	 */
	public short getRolled() {
		return rolled;
	}
	
	/**
	 * Gets the number of kept dice.
	 * @return the kept count.
	 */
	public short getKept() {
		return kept;
	}

	/**
	 * Gets the number of sides on the dice.
	 * @return the sides count.
	 */
	public short getSides() {
		return sides;
	}

	/**
	 * Gets the modifier for the roll.
	 * @return the modifier.
	 */
	public Modifier getModifier() {
		return modifier;
	}
	
	/** 
	 * Gets the reroll object for this roll, if any.
	 * @return the reroll.
	 */
	public Reroll getReroll() {
		return reroll;
	}
	
	/** 
	 * Gets the explosion object for this roll, if any.
	 * @return the explosion.
	 */
	public Explosion getExplosion() {
		return explosion;
	}
	
	
	public List<GroupResult> performRoll(int groupCount, Random random, Statistics statistics) {
		List<GroupResult> groups = new ArrayList<GroupResult>();
		statistics.addToGroups(groupCount);
		for (int i = 0; i < groupCount; i++) {
			// Generate the rolled dice.
			List<Integer> dice = new ArrayList<Integer>();
			for (int j = 0; j < rolled; j++) {
				int rolled = rollDie(sides, random, reroll, explosion, statistics);
				dice.add(rolled);
			}
			Collections.sort(dice);
			Collections.reverse(dice);
			
			long natural = Utils.sumFirst(dice, kept);
			statistics.registerRoll(rolled + "-" + kept, natural);
			long modified = modifier.apply(natural);
			// Check for criticals.
			if ((natural == rolled) && personality.useCritSuccesses()) {
				groups.add(new GroupResult(dice, natural, modified, true, false));
			} else if ((natural == (rolled * sides)) && personality.useCritFailures()) {
				groups.add(new GroupResult(dice, natural, modified, false, true));
			} else {
				groups.add(new GroupResult(dice, natural, modified, false, false));
			}
		}
		
		return groups;
	}
	
	/**
	 * Rolls a d10, recursively handling explosions. 
	 * @param diceType The type of dice to roll.
	 * @param random The {@link Random} to use for generating numbers.
	 * @param behaviors The behaviors of the dice.
	 * @param statistics The statistics for roll tracking.
	 * @return the value of the roll.
	 */
	private int rollDie(int diceType, Random random, Reroll reroll, Explosion explosion, Statistics statistics) {
		int roll = random.getRoll(diceType);
		
		// Check for reroll.
		if (null != reroll && reroll.needsRerolled(roll)) {
			roll = random.getRoll(diceType);
			while (reroll.needsRerolled(roll) && reroll.forceGoodValue()) {
				roll = random.getRoll(diceType);
			}
		}
		
		// Check for explosion.
		if (null != explosion && explosion.shouldExplode(roll)) {
			int nextDie = random.getRoll(diceType);
			roll += nextDie;
			while (explosion.shouldExplode(nextDie)) {
				nextDie = random.getRoll(diceType);
				roll += nextDie;
			}
		}
	
		return roll;
	}
	
	/** Class that represents the result of a rolled group of dice. */
	public static class GroupResult {
		/** The sorted group of rolled dice. */ 
		private final List<Integer> dice;
		/** The summed natural value of the group. */
		private final long natural;
		/** The modified value of the group. */
		private final long modified;
		/** Whether the roll was a critical failure. */
		private final boolean criticalFailure;
		/** Whether the roll was a critical success. */
		private final boolean criticalSuccess;
		
		/**
		 * Constructs a new group.
		 * @oaram dice The rolled dice.
		 * @param natural The natural value of the group.
		 * @param modified The modified value of the group.
		 * @param criticalFailure If the roll was a critical failure.
		 * @param criticalSuccess If the roll was a critical success.
		 */
		private GroupResult(List<Integer> dice, long natural, long modified, boolean criticalFailure, boolean criticalSuccess) {
			this.dice = dice;
			this.natural = natural;
			this.modified = modified;
			this.criticalFailure = criticalFailure;
			this.criticalSuccess = criticalSuccess;
		}

		/**
		 * Gets the list of dice rolled for this group.
		 * @return the list of dice.
		 */
		public List<Integer> getDice() {
			return dice;
		}

		/**
		 * Gets the summed natural roll for this group.
		 * @return the natural value.
		 */
		public long getNatural() {
			return natural;
		}

		/**
		 * Gets the summed and modified natural roll for this group.
		 * @return the modified value.
		 */
		public long getModified() {
			return modified;
		}

		/**
		 * Checks whether the roll was a critical failure.
		 * @return true if the roll was a critical failure, false otherwise.
		 */
		public boolean isCriticalFailure() {
			return criticalFailure;
		}

		/**
		 * Checks whether the roll was a critical success.
		 * @return true if the roll was a critical success, false, otherwise.
		 */
		public boolean isCriticalSuccess() {
			return criticalSuccess;
		}
	}
}