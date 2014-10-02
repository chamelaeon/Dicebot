package com.chamelaeon.dicebot.commands;

import java.util.List;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.api.Personality;
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
		Personality personality = event.getBot().getPersonality();
		event.respond(personality.getMessage("StatusGeneral"));
		event.respond(personality.getMessage("StatusChannelCount", 
		        event.getBot().getUserChannelDao().getAllChannels().size()));
		event.respond(personality.getMessage("StatusRolledCount", event.getBot().getStatistics().getGroups(), 
		        event.getBot().getStatistics().getDice()));
	}
}