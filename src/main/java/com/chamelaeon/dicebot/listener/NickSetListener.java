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
public class NickSetListener extends ListenerAdapter<Dicebot> implements NickHandlingListener {
    /** The nicks to use for the dicebot. */
	private final String[] nicks;
	/** The current index for nicks. */
	private int index = 0;
	/** Autosuffixes for defaulting nicks. */
	private int nickSuffix = 1;
	
	/**
	 * Constructor.
	 * @param nicks The nicks to use for the dicebot.
	 */
	public NickSetListener(String[] nicks) {
		this.nicks = nicks;
	}
	
	@Override
	public void onNickAlreadyInUse(NickAlreadyInUseEvent<Dicebot> event) throws Exception {
		String nextNick;
	    if (++index < nicks.length) {
			System.out.println(event.getUsedNick() + " already in use. Retrying with " + nicks[index] + ".");
			nextNick = nicks[index];
		} else {
			System.out.println("All given nicks are currently being used! Defaulting to just appending numbers...");
			nextNick = event.getBot().getConfiguration().getName() + nickSuffix++;
		}
	    
	    event.getBot().setNick(nextNick);
		event.respond(nextNick);
	}

    @Override
    public void resetNickIndex() {
        index = 0;
    }
}
