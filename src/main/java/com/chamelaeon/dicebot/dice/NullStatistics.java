/**
 * 
 */
package com.chamelaeon.dicebot.dice;


/**
 * A Statistics object that doesn't track anything.
 * @author Chamelaeon
 */
public class NullStatistics implements IStatistics {
    @Override
    public void registerRoll(int diceType, int rollValue) {
        // Do nothing.
    }

    @Override
    public void registerRoll(String rollName, long rollValue) {
        // Do nothing.
    }
}
