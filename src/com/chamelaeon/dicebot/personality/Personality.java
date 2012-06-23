package com.chamelaeon.dicebot.personality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.chamelaeon.dicebot.InputException;
import com.chamelaeon.dicebot.random.Random;


public abstract class Personality {

	/** The exception texts, mapped by key. */
	final Map<String, String> exceptionTexts;
	
	/** The roll outputs, mapped by key. */
	final Map<String, String> rollOutputs;
	
	/** The available critical failure message list. */
	final List<String> criticalFailures;
	
	/** The available critical failure message list. */
	final List<String> criticalSuccesses;
	
	/** Whether or not to use critical success messages. */
	final AtomicBoolean useCritSuccesses;
	
	/** Whether or not to use critical failure messages. */
	final AtomicBoolean useCritFailures;
	
	/** The path to the card database. */
	final String cardPath;
	
	Personality(String cardPath) {
		exceptionTexts = new HashMap<String, String>();
		rollOutputs = new HashMap<String, String>();
		criticalFailures = new ArrayList<String>();
		criticalSuccesses = new ArrayList<String>();
		useCritSuccesses = new AtomicBoolean(true);
		useCritFailures = new AtomicBoolean(true);
		this.cardPath = cardPath;
	}
	
	/**
	 * Gets the user-facing {@link InputException} represented by the key.  
	 * @param key The exception's individual key.
	 * @param params The parameters to fill out for the exception.
	 * @return the exception to throw up to where a user can see it.
	 */
	public InputException getException(String key, Object... params) {
		return new InputException(String.format(exceptionTexts.get(key), params));
	}

	/**
	 * Gets the roll result text for the given key.
	 * @param key The roll result's key.
	 * @param params The parameters to fill out for the roll result.
	 * @return the filled-out roll results.
	 */
	public String getRollResult(String key, Object... params) {
		return String.format(rollOutputs.get(key), params);
	}
	
	/**
	 * Returns whether or not this personality allows critical success messages or not.
	 * @return true if critical success messages are allowed, false otherwise.
	 */
	public boolean useCritSuccesses() {
		return useCritSuccesses.get();
	}
	
	/**
	 * Returns whether or not this personality allows critical failure messages or not.
	 * @return true if critical failure messages are allowed, false otherwise.
	 */
	public boolean useCritFailures() {
		return useCritFailures.get();
	}

	/**
	 * Picks a random critical failure line from the available selection.
	 * @param random The random to use.
	 * @return the selected critical failure line.
	 */
	public String chooseCriticalFailureLine(Random random) {
		return criticalFailures.get(random.getRoll(criticalFailures.size()) - 1);
	}
	
	/**
	 * Picks a random critical success line from the available selection.
	 * @param random The random to use.
	 * @return the selected critical success line.
	 */
	public String chooseCriticalSuccessLine(Random random) {
		return criticalSuccesses.get(random.getRoll(criticalSuccesses.size()) - 1);
	}

	/**
	 * Gets a status message.
	 * @return the status.
	 */
	public abstract String getStatus();

	/**
	 * Gets the paths to cards for drawing.
	 * @return the card path.
	 */
	public String getCardPath() {
		return cardPath;
	}
}