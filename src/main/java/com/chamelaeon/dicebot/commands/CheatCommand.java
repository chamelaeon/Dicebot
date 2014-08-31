/**
 * 
 */
package com.chamelaeon.dicebot.commands;

import java.util.List;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;


/**
 * A command for cheaters.
 * @author Chamelaeon
 */
public class CheatCommand extends DicebotListenerAdapter {
    /** Constructor. */
	public CheatCommand() {
		super("!cheat", new HelpDetails("cheat", "The cheatiest of commands. For losers only."));
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
		event.respond(event.getBot().getPersonality().getMessage("Cheat"));
	} 
}
