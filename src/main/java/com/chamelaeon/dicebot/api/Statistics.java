package com.chamelaeon.dicebot.api;


public interface Statistics {

    /**
     * Registers the given roll. This method registers SINGLE DIE ROLLS, i.e. 1d6 or 1d10!
     * It should not be used to track complete rolls such as 2d6 or 1k1.
     * @param diceType The type of die rolled.
     * @param rollValue The value of the roll.
     */
    public void registerRoll(int diceType, int rollValue);

    /**
     * Registers the given roll. This method registers ACTUAL ROLLS, i.e. 2d6 or 1k1!
     * It will track based on the string name of the roll.
     * 
     * @param diceName The name of the roll.
     * @param rollValue The value of the roll.
     */
    public void registerRoll(String rollName, long rollValue);

    /**
     * Gets the number of rolled dice.
     * @return the dieCount.
     */
    public long getDice();

    /**
     * Gets the number of rolled groups.
     * @return the groups.
     */
    public long getGroups();
    
    /**
     * Increases the groups count by the given amount.
     * @param modifier the amount to increase the groups by.
     */
    public void addToGroups(int modifier);

    /**
	 * Gets the average roll for the given roll name. If no rolls of that
	 * type have been rolled, the average will be 0;
	 * @param rollName The name of the roll to check the average for.
	 * @return the average.
	 */
	public double getAverage(String rollName);

	/**
	 * Gets the average roll for the type of die rolled. If no dice of that
	 * type have been rolled, the average will be 0;
	 * @param dieType The type of die to check the average for.
	 * @return the average.
	 */
	public double getAverage(int dieType);
}