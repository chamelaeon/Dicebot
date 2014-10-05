package com.chamelaeon.dicebot.dice.behavior;

/**
 * Describes a behavior which causes reroll. 
 * @author Chamelaeon
 */
public interface Reroll {
	/**
	 * Checks to see if a value needs rerolled.
	 * @param natural The value to check for rerolling.
	 * @return true if the value needs rerolled, false otherwise.
	 */
	public boolean needsRerolled(int natural);

	/**
	 * Whether the reroll should continue until there is a value which does not
	 * pass the {@link #needsRerolled(int)} check.
	 * @return true if the reroll should continue, false otherwise.
	 */
	public boolean forceGoodValue();
	
	/**
     * Gets the threshold for the reroll.
     * @return the threshold value.
     */
    public Integer getThreshold();
	
	/**
	 * Checks to see if the given reroll condition cannot be satisfied (i.e. will always reroll).
	 * @param maxValue The maximum value of the dice.
	 * @return true if the reroll cannot be satisfied, false otherwise.
	 */
	public boolean cannotBeSatisfied(int maxValue);
}