package com.chamelaeon.dicebot.dice;

/** The result of a die roll. */
public class DieResult implements Comparable<DieResult> {
	/** The result of the roll. */
	private int result;
	/** Whether the die was rerolled. */
	private boolean wasRerolled;

	/**
	 * Constructs a new DieResult.
	 * @param result The result of the roll.
	 * @param wasRerolled Whether the die was rerolled.
	 */
	public DieResult(int result, boolean wasRerolled) {
		this.result = result;
		this.wasRerolled = wasRerolled;
	}
	
	/**
	 * Returns the result of the roll.
	 * @return the result.
	 */
	public int getResult() {
		return result;
	}
	
	/**
	 * Returns whether the result wsa because of a reroll.
	 * @return true if it was rerolled, false otherwise.
	 */
	public boolean wasRerolled() {
		return wasRerolled;
	}

	@Override
	public int compareTo(DieResult o) {
		return Integer.valueOf(result).compareTo(o.result);
	}
	
	@Override
	public String toString() {
		String retVal = Integer.toString(result);
		if (wasRerolled) {
			retVal += "*";
		}
		return retVal;
	}
}
