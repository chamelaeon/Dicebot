/**
 * 
 */
package com.chamelaeon.dicebot.statistics;

import com.chamelaeon.dicebot.api.Statistics;


/**
 * A Statistics object that doesn't track anything.
 * @author Chamelaeon
 */
public class NullStatistics implements Statistics {
    @Override
    public void registerRoll(int diceType, int rollValue) {
        // Do nothing.
    }

    @Override
    public void registerRoll(String rollName, long rollValue) {
        // Do nothing.
    }

    public long getDice() {
        return 0;
    }

    public long getGroups() {
        return 0;
    }

    @Override
    public void addToGroups(int modifier) {
        // Do nothing.
    }
}
