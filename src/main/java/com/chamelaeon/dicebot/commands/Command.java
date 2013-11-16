package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.Dicebot;


/** A command that the bot can perform. */
public interface Command {
	/**
	 * Executes the given command, with the given matcher and the given source and user.
	 * @param dicebot The dicebot that received the signal for this command.
	 * @param matcher The matcher which matched the command.
	 * @param source The source of the command.
	 * @param user The user who called the command.
	 * @return the string to return to the user.
	 */
	public String execute(Dicebot dicebot, Matcher matcher, String source, String user);
	
	/**
	 * Gets a description of the command for the help file.
	 * @return the description.
	 */
	public String getDescription();
	
	/**
	 * Get the regexp which matches this command (excluding !).
	 * @return the regexp.
	 */
	public String getRegexp();
}