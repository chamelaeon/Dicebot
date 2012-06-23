package com.chamelaeon.dicebot;

/** Exception for exception handling on the input values. */
public class InputException extends Exception {
	/** Serial UID. */
	private static final long serialVersionUID = -5694304623380196996L;

	/**
	 * Constructs a new {@link InputException}.
	 * @param message The message to display to the user.
	 */
	public InputException(String message) {
		super(message);
	}
}