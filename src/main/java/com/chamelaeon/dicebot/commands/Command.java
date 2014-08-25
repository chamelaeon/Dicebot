package com.chamelaeon.dicebot.commands;

import org.pircbotx.hooks.Listener;

import com.chamelaeon.dicebot.Dicebot;

/**
 * A command that the bot can perform.
 * @author Chamelaeon
 */
public interface Command extends Listener<Dicebot> {

	public HelpDetails getHelpDetails();
}