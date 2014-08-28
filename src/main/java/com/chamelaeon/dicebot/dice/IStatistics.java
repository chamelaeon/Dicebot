package com.chamelaeon.dicebot.dice;

public interface IStatistics {

    /**
     * Registers the given roll. This method registers SINGLE DIE ROLLS, i.e. 1d6 or 1d10!
     * It should not be used to track complete rolls such as 2d6 or 1k1.
     * @param diceType The type of die rolled.
     * @param rollValue The value of the roll.
     */
    public abstract void registerRoll(int diceType, int rollValue);

    /**
     * Registers the given roll. This method registers ACTUAL ROLLS, i.e. 2d6 or 1k1!
     * It will track based on the string name of the roll.
     * 
     * @param diceName The name of the roll.
     * @param rollValue The value of the roll.
     */
    public abstract void registerRoll(String rollName, long rollValue);

}