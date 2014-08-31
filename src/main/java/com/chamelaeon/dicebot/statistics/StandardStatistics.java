package com.chamelaeon.dicebot.statistics;

import java.util.HashMap;
import java.util.Map;

import com.chamelaeon.dicebot.api.Statistics;

/** Data struct for storing statistics. */
public class StandardStatistics implements Statistics {
	/** The number of groups that have been rolled. */
	private long groups = 0;
	/** The number of dice that have been rolled, by type. */
	private final Map<Integer, Integer> dice;
	/** The number of registered complete rolls that have been rolled, by name. */
	private final Map<String, Integer> namedRolls;
	/** The map holding die type averages. */
	private final Map<Integer, Double> averages;
	/** The map holding die type averages. */
	private final Map<String, Double> namedAverages;
	
	/** Public constructor. */
	public StandardStatistics() {
		averages = new HashMap<Integer, Double>();
		dice = new HashMap<Integer, Integer>();
		namedRolls = new HashMap<String, Integer>();
		namedAverages = new HashMap<String, Double>();
	}
	
	@Override
    public long getGroups() {
		return groups;
	}
	
	@Override
    public long getDice() {
		long total = 0;
		for (Integer rolled : dice.values()) {
			total += rolled;
		}
		return total;
	}
	
	/** Increments the groups count. */
	public void incrementGroups() {
		groups++;
	}
	
	@Override
    public void registerRoll(int diceType, int rollValue) {
		Integer count = dice.get(diceType);
		if (null != count) {
			dice.put(diceType, count + 1);
		} else {
			dice.put(diceType, 1);
			averages.put(diceType, Double.valueOf(rollValue));
			return;
		}

		// If the dice map has a value for it, the average one will too.
		Double average = averages.get(diceType);
		double newAverage = ((average * count) + rollValue) / (count + 1.0);
		averages.put(diceType, newAverage);
	}
	
	@Override
    public void registerRoll(String rollName, long rollValue) {
		Integer count = namedRolls.get(rollName);
		if (null != count) {
			namedRolls.put(rollName, count + 1);
		} else {
			namedRolls.put(rollName, 1);
			namedAverages.put(rollName, Double.valueOf(rollValue));
			return;
		}

		// If the dice map has a value for it, the average one will too.
		Double average = namedAverages.get(rollName);
		double newAverage = ((average * count) + rollValue) / (count + 1.0);
		namedAverages.put(rollName, newAverage);
	}
	
	@Override
	public void addToGroups(int modifier) {
		groups += modifier;
	}

	/**
	 * Gets the average roll for the type of die rolled. If no dice of that
	 * type have been rolled, the average will be 0;
	 * @param dieType The type of die to check the average for.
	 * @return the average.
	 */
	public double getAverage(int dieType) {
		Double average = averages.get(dieType);
		if (null == average) {
			return 0;
		} else {
			return average;
		}
	}
	
	/**
	 * Gets the average roll for the given roll name. If no rolls of that
	 * type have been rolled, the average will be 0;
	 * @param rollName The name of the roll to check the average for.
	 * @return the average.
	 */
	public double getAverage(String rollName) {
		Double average = namedAverages.get(rollName);
		if (null == average) {
			return 0;
		} else {
			return average;
		}
	}
}