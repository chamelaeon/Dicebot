package com.chamelaeon.dicebot.dice;

import java.util.List;

/**
 * Class that represents the result of a rolled group of dice.
 * @author Chamelaeon
 */
public class GroupResult implements Comparable<GroupResult> {
	/** The sorted group of rolled dice. */
	private final List<DieResult> dice;
	/** The summed natural value of the group. */
	private final long natural;
	/** The modified value of the group. */
	private final long modified;

	/**
	 * Constructs a new group.
	 * @param dice The rolled dice.
	 * @param natural The natural value of the group.
	 * @param modified The modified value of the group.
	 */
	GroupResult(List<DieResult> dice, long natural, long modified) {
		this.dice = dice;
		this.natural = natural;
		this.modified = modified;
	}

	/**
	 * Gets the list of dice rolled for this group.
	 * @return the list of dice.
	 */
	public List<DieResult> getDice() {
		return dice;
	}

	/**
	 * Gets the summed natural roll for this group.
	 * @return the natural value.
	 */
	public long getNatural() {
		return natural;
	}

	/**
	 * Gets the summed and modified natural roll for this group.
	 * @return the modified value.
	 */
	public long getModified() {
		return modified;
	}

    @Override
    public int compareTo(GroupResult o) {
        return Long.compare(natural, o.getNatural());
    }
}
