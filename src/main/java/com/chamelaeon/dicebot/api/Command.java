package com.chamelaeon.dicebot.api;

import org.pircbotx.hooks.Listener;


/**
 * A command that the bot can perform.
 * @author Chamelaeon
 */
public interface Command extends Listener<Dicebot> {

    /**
     * Gets the help details for this command.
     * @return the help details.
     */
	public HelpDetails getHelpDetails();
}