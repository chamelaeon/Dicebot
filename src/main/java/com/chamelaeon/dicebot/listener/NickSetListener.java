/**
 * 
 */
package com.chamelaeon.dicebot.listener;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;

import com.chamelaeon.dicebot.api.Dicebot;

/**
 * A listener that tracks if a nick is already in use and provides another.
 * @author Chamelaeon
 */
public class NickSetListener extends ListenerAdapter<Dicebot> {
    /** The nicks to use for the dicebot. */
	private final String[] nicks;
	/** The current index for nicks. */
	private int index = 0;
	
	/**
	 * Constructor.
	 * @param nicks The nicks to use for the dicebot.
	 */
	public NickSetListener(String[] nicks) {
		this.nicks = nicks;
	}
	
	@Override
	public void onNickAlreadyInUse(NickAlreadyInUseEvent<Dicebot> event) throws Exception {
	    if (index < nicks.length) {
			System.out.println(event.getUsedNick() + " already in use. Retrying with " + nicks[++index] + ".");
			event.respond(nicks[index]);
		} else {
			System.out.println("All given nicks are currently being used! Defaulting to just appending numbers...");
			event.respond(event.getAutoNewNick());
		}
	}

	/**
	 * Resets the nick index, allowing the bot to start back at the beginning of its nicklist and 
	 * potentially reclaim a nick that has pinged out. 
	 */
    public void resetNickIndex() {
        index = 0;
    }
}