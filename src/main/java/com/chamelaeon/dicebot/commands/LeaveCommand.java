package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.Dicebot;


/** A command to mute the bot for a certain channel. */
public class LeaveCommand implements Command {
	@Override
	public String execute(Dicebot dicebot, Matcher matcher, String source, String user) {
		dicebot.removeChannel(source);
		dicebot.partChannel(source);
		return null;
	}

	@Override
	public String getDescription() {
		return "Makes the bot leave the channel that this command is used in.";
	}
	
	@Override
	public String getRegexp() {
		return "leave";
	}
}