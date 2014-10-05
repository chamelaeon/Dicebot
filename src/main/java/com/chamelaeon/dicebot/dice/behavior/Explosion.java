package com.chamelaeon.dicebot.dice.behavior;

/**
 * Describes a behavior that causes explosions.
 * @author Chamelaeon
 */
public interface Explosion {
	/**
	 * Checks to see if the value should explode.
	 * @param natural The value to check for explosion.
	 * @return true if the value should explode, false otherwise.
	 */
	public boolean shouldExplode(int natural);
	
	/**
     * Gets the threshold for the reroll.
     * @return the threshold value.
     */
    public Integer getThreshold();
	
	/**
	 * Checks to see if the given explosion will explode infinitely for the given minimum dice value.
	 * @param minValue The minimum dice value.
	 * @return true if the explosion will always happen, false otherwise.
	 */
	public boolean explodesInfinitely(int minValue);
}