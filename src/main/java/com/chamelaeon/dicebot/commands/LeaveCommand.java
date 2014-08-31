package com.chamelaeon.dicebot.commands;

import java.util.List;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;


/** 
 * A command to make the bot leave a certain channel.
 * @author Chamelaeon
 */
public class LeaveCommand extends DicebotListenerAdapter {
	
	/** Constructor. */
	public LeaveCommand() {
		super("!leave", 
				new HelpDetails("leave", "Makes the bot leave the channel that this command is used in."));
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
		if (null != event.getChannel()) {
			// TODO: Fix this to personality.
		    event.getChannel().send().part("I know when I'm not wanted, suckers.");
		}
	}
}