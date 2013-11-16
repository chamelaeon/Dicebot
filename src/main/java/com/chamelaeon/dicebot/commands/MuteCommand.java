package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.Dicebot;


/** A command to mute the bot for a certain channel. */
public class MuteCommand implements Command {
	@Override
	public String execute(Dicebot dicebot, Matcher matcher, String source, String user) {
		if (null == dicebot.getChannelStatus(source)) {
			dicebot.addChannel(source);
		}

		dicebot.sendAction(source, "shuts up for " + source + ".");
		dicebot.setChannelState(source, false);
		return null;
	}
	
	@Override
	public String getDescription() {
		return "Mutes the bot for the channel (or player!) that this command is used in.";
	}
	
	@Override
	public String getRegexp() {
		return "mute";
	}
}