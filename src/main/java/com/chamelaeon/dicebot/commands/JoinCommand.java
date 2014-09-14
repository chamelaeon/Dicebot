package com.chamelaeon.dicebot.commands;

import java.util.List;

import org.pircbotx.hooks.events.InviteEvent;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;


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
			
			try {
			    event.getBot().sendIRC().joinChannel(channel);
			} catch (IllegalStateException ise) {
			    // Personality & better.
			    event.respond("Love to, but I'm at my max number of channels and I can't leave any.");
			}
		}
	}

	@Override
	public void onInvite(InviteEvent<Dicebot> event) throws Exception {
		event.getBot().sendIRC().joinChannel(event.getChannel());
	}
}