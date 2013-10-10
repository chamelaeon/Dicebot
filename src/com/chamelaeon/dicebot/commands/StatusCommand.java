package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.Dicebot;

/** A command to display help to a user. */
public class StatusCommand implements Command {
	@Override
	public String execute(Dicebot dicebot, Matcher matcher, String source, String user) {
		// TODO: Move these into Personality. 
		dicebot.sendMessage(user, dicebot.getStatus());
		dicebot.sendMessage(user, "I'm sitting in " + dicebot.getChannelCount() + " channels, watching the dice go by.");
		dicebot.sendMessage(user, "I've rolled " + dicebot.getStatistics().getGroups() + " groups and " + dicebot.getStatistics().getDice() + " actual dice since being turned on.");
		return null;
	}
	
	@Override
	public String getDescription() {
		return "Displays status and statistics for the bot.";
	}
	
	@Override
	public String getRegexp() {
		return "status";
	}
}