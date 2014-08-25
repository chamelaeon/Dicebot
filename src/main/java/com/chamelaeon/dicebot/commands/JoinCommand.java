package com.chamelaeon.dicebot.commands;

import java.util.List;

import org.pircbotx.hooks.events.InviteEvent;

import com.chamelaeon.dicebot.Dicebot;
import com.chamelaeon.dicebot.listener.DicebotGenericEvent;
import com.chamelaeon.dicebot.listener.DicebotListenerAdapter;


/**
 * A command to join a channel.
 * @author Chamelaeon
 */
public class JoinCommand extends DicebotListenerAdapter {
    
    /** Constructor. */
	public JoinCommand() {
		super("!join (#[a-zA-Z0-9-_]+)", new HelpDetails("join", "Makes the bot join the specified channel, if it can."));
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
		if (groups.size() >= 1) {
			String channel = groups.get(1);
			event.getBot().sendIRC().joinChannel(channel);
		}
	}

	@Override
	public void onInvite(InviteEvent<Dicebot> event) throws Exception {
		event.getBot().sendIRC().joinChannel(event.getChannel());
	}
}