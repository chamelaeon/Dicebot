package com.chamelaeon.dicebot.commands;

import java.util.List;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;


/** 
 * A command to display the bot's status to a user.
 * @author Chamelaeon
 */
public class StatusCommand extends DicebotListenerAdapter {

    /** Constructor. */
	public StatusCommand() {
		super("!status", new HelpDetails("status", "Displays status and statistics for the bot."));
	}
	
	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
		// TODO: Move these into Personality. 
		event.respond(event.getBot().getPersonality().getStatus());
		event.respond("I'm sitting in " + event.getBot().getUserChannelDao().getAllChannels().size() 
				+ " channels, watching the dice go by.");
		event.respond("I've rolled " + event.getBot().getStatistics().getGroups() + " groups and " 
				+ event.getBot().getStatistics().getDice() + " actual dice since being turned on.");
	}
}