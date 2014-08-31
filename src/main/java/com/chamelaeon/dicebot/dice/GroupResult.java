package com.chamelaeon.dicebot.dice;

import java.util.List;

/** 
 * Class that represents the result of a rolled group of dice.
 * @author Chamelaeon 
 */
public class GroupResult {
	/** The sorted group of rolled dice. */ 
	private final List<DieResult> dice;
	/** The summed natural value of the group. */
	private final long natural;
	/** The modified value of the group. */
	private final long modified;
	/** Whether the roll was a critical failure. */
	private final boolean criticalFailure;
	/** Whether the roll was a critical success. */
	private final boolean criticalSuccess;
	
	/**
	 * Constructs a new group.
	 * @oaram dice The rolled dice.
	 * @param natural The natural value of the group.
	 * @param modified The modified value of the group.
	 * @param criticalFailure If the roll was a critical failure.
	 * @param criticalSuccess If the roll was a critical success.
	 */
	GroupResult(List<DieResult> dice, long natural, long modified, boolean criticalFailure, boolean criticalSuccess) {
		this.dice = dice;
		this.natural = natural;
		this.modified = modified;
		this.criticalFailure = criticalFailure;
		this.criticalSuccess = criticalSuccess;
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

	/**
	 * Checks whether the roll was a critical failure.
	 * @return true if the roll was a critical failure, false otherwise.
	 */
	public boolean isCriticalFailure() {
		return criticalFailure;
	}

	/**
	 * Checks whether the roll was a critical success.
	 * @return true if the roll was a critical success, false, otherwise.
	 */
	public boolean isCriticalSuccess() {
		return criticalSuccess;
	}
}