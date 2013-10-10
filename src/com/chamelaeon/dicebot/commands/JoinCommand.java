package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.Dicebot;

/** A command to display help to a user. */
public class JoinCommand implements Command {
	@Override
	public String execute(Dicebot dicebot, Matcher matcher, String source, String user) {
		if (matcher.groupCount() >= 1) {
			String channel = matcher.group(1).trim();
			dicebot.addChannel(channel);
			dicebot.setChannelState(channel, true);
			dicebot.joinChannel(channel);
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "Makes the bot join the specified channel, if it can.";
	}
	
	@Override
	public String getRegexp() {
		return "join (#[a-zA-Z0-9-_]+)";
	}
}