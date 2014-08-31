package com.chamelaeon.dicebot.random;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.statistics.NullStatistics;

/** 
 * A Random implementation that just uses the base Java Random.
 * @author Chamelaeon
 */
public class BasicRandom implements Random {
	/** The Random object for the roller. */
	private final java.util.Random random;
	
	/** Creates a new {@link BasicRandom}. */
	public BasicRandom() {
		this.random = new java.util.Random();
	}
	
	@Override
    public int getRoll(int diceType) {
	    return getRoll(diceType, new NullStatistics());
    }

    @Override
	public int getRoll(int diceType, Statistics statistics) {
		int val = random.nextInt(diceType) + 1;
		statistics.registerRoll(diceType, val);
		return val;
	}
}