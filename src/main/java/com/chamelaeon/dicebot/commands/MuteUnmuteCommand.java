package com.chamelaeon.dicebot.commands;

import java.util.List;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;
import com.chamelaeon.dicebot.framework.MuteableChannel;


/** 
 * A command to mute or unmute the bot for a certain channel.
 * @author Chamelaeon
 */
public class MuteUnmuteCommand extends DicebotListenerAdapter {
	
	/** Constructor. */
	public MuteUnmuteCommand() {
		super("!(mute|unmute)", 
				new HelpDetails("mute", "Mutes or unmutes the bot for the channel that this command is used in."));
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
		if (null != event.getChannel()) {
			MuteableChannel channel = (MuteableChannel) event.getChannel();

			if ("mute".equals(groups.get(1)) && !channel.isMuted()) {
				channel.send().action("shuts up for " + channel.getName());
				channel.setMuted(true);
			} else if ("unmute".equals(groups.get(1)) && channel.isMuted()) {
			    channel.setMuted(false);
			    channel.send().action("is free to talk in " + channel.getName() + " again!");
			}
		}
	}
}