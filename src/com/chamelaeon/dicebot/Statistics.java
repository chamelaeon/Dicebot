package com.chamelaeon.dicebot;

import java.util.HashMap;
import java.util.Map;

/** Data struct for storing statistics. */
public class Statistics {
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
	public Statistics() {
		averages = new HashMap<Integer, Double>();
		dice = new HashMap<Integer, Integer>();
		namedRolls = new HashMap<String, Integer>();
		namedAverages = new HashMap<String, Double>();
	}
	
	/**
	 * Gets the number of rolled groups.
	 * @return the groups.
	 */
	public long getGroups() {
		return groups;
	}
	
	/**
	 * Gets the number of rolled dice.
	 * @return the dieCount.
	 */
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
	
	/**
	 * Registers the given roll. This method registers SINGLE DIE ROLLS, i.e. 1d6 or 1d10!
	 * It should not be used to track complete rolls such as 2d6 or 1k1.
	 * @param diceType The type of die rolled.
	 * @param rollValue The value of the roll.
	 */
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
	
	/**
	 * Registers the given roll. This method registers ACTUAL ROLLS, i.e. 2d6 or 1k1!
	 * It will track based on the string name of the roll.
	 * 
	 * @param diceName The name of the roll.
	 * @param rollValue The value of the roll.
	 */
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
	
	/**
	 * Increases the groups count by the given amount.
	 * @param modifier the amount to increase the groups by.
	 */
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