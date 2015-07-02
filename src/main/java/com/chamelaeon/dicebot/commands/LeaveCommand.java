package com.chamelaeon.dicebot.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
		super("!leave[ ]*([a-zA-Z0-9-_]*)", 
				new HelpDetails("leave", "Makes the bot leave the channel that this command is used in. If a nick is provided, " +
						"only a bot with that nick will leave.",
				        Arrays.asList("!leave", "!leave botnick")));
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
		if (groups.size() >= 1) {
		    String nickToLeave = groups.get(1);
		    
		    if (!StringUtils.isEmpty(nickToLeave) && !event.getBot().getNick().equalsIgnoreCase(nickToLeave)) {
		        // If the mentioned name is not this bot's nick, ignore the command.
		        return;
		    }
		}
	    
	    if (null != event.getChannel()) {
		    event.getChannel().send().part(event.getBot().getPersonality().getMessage("Leave"));
		}
	}
}