package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.Dicebot;

/** A command to mute the bot for a certain channel. */
public class UnmuteCommand implements Command {
	@Override
	public String execute(Dicebot dicebot, Matcher matcher, String source, String user) {
		if (null == dicebot.getChannelStatus(source)) {
			dicebot.addChannel(source);
		}
		
		dicebot.setChannelState(source, true);
		return "is free to talk in " + source + " again!";
	}

	@Override
	public String getDescription() {
		return "Unmutes the bot for the channel that this command is used in.";
	}
	
	@Override
	public String getRegexp() {
		return "unmute";
	}
}