package com.chamelaeon.dicebot.dice;

import java.util.List;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
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
	 * @param number The number of dice to roll. 
	 * @param random The {@link Random} to use for generating numbers.
	 * @param reroll The reroll behaviors of the dice.
	 * @param explosion The explosion behaviors of the dice.
	 * @param statistics The statistics for roll tracking.
	 * @return the result of of each die rolled, in order.
	 */
	public List<DieResult> rollDice(int number, Random random, Reroll reroll, Explosion explosion, Statistics statistics);
}
