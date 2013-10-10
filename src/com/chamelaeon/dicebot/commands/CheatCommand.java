/**
 * 
 */
package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.Dicebot;

/**
 * A command for cheaters.
 */
public class CheatCommand implements Command {

	@Override
	public String execute(Dicebot dicebot, Matcher matcher, String source, String user) {
		dicebot.sendMessage(source, dicebot.getPersonalityMessage("Cheat"));
		return null;
	}

	@Override
	public String getDescription() {
		return "The cheatiest of commands. For losers only.";
	}

	@Override
	public String getRegexp() {
		return "cheat";
	}

}
