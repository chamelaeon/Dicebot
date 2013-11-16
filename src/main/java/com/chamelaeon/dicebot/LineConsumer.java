package com.chamelaeon.dicebot;

import java.util.regex.Matcher;

/** Describes objects which consume lines from IRC. */
public interface LineConsumer {
	
	/**
	 * Consumes the line using the given matcher, and returns a string to print to send as an action.
	 * @param matcher The matcher containing the regexp and the incoming string.
	 * @param source The source of the line. 
	 * @param user The user who performed the action.
	 * @return the result of the action.
	 */
	public String consume(Matcher matcher, String source, String user) throws InputException;
}