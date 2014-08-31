package com.chamelaeon.dicebot.random;

import com.chamelaeon.dicebot.api.Statistics;

/**
 * Generates random numbers, for simulating dice rolling.
 * @author Chamelaeon
 */
public interface Random {

    /**
     * Returns the next roll from the random number generator, in the range [1-diceType]. The roll is not tracked
     * by statistics.
     * @param diceType The type of dice to roll (e.g. d6).
     * @return the result of the roll.
     */
    public int getRoll(int diceType);
    
	/**
	 * Returns the next roll from the random number generator, in the range [1-diceType]. The roll is tracked
	 * in the given Statistics object.
	 * @param diceType The type of dice to roll (e.g. d6).
	 * @param statistics The statistics to use to track the roll.
	 * @return the result of the roll.
	 */
	public int getRoll(int diceType, Statistics statistics);	
}
