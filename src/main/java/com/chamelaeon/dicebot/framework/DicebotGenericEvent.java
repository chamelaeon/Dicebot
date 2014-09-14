/**
 * 
 */
package com.chamelaeon.dicebot.framework;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.chamelaeon.dicebot.api.Dicebot;

/**
 * A wrapper for the PircBotX generic message event, with a custom ability to respond.
 * @author Chamelaeon
 */
public class DicebotGenericEvent<T extends Dicebot> implements GenericMessageEvent<T> {
    /** The original generic message event. */
	private final GenericMessageEvent<T> event;
	/** The user who generated the event. */
	private final User user;
	/** The channel for the event, if any. */
	private final Channel channel;
	
	/**
	 * Constructor that requires only a channel and an event.
	 * @param channel The channel that the event occurred in.
	 * @param event The event.
	 */
	public DicebotGenericEvent(Channel channel, GenericMessageEvent<T> event) {
	    this(event.getUser(), channel, event);
	}
	
	/**
	 * Constructor that requires only a user and an event.
	 * @param user The user who generated the event.
	 * @param event The event.
	 */
	public DicebotGenericEvent(User user, GenericMessageEvent<T> event) {
		this(user, null, event);
	}
	
	/**
	 * Constructor.
	 * @param user The user who generated the event.
	 * @param channel The channel that the event occurred in.
     * @param event The event.
	 */
	public DicebotGenericEvent(User user, Channel channel, GenericMessageEvent<T> event) {
	    this.channel = channel;
        this.user = user;
        this.event = event;
	}
	
	/**
	 * Returns the channel the event was in, or null if it was a private message.
	 * @return the channel the event was in or null.
	 */
	public Channel getChannel() {
		return channel;
	}
	
	@Override
	public User getUser() {
		return user;
	}

	@Override
	public void respond(String response) {
	    if (null != channel) {
	       channel.send().message(response);
	    } else {
	        user.send().message(response);
	    }
	}
	
	/**
	 * Sends a response as an IRC action (i.e. the /me command). 
	 * This will automatically prefix the response with the dicebot's nick.
	 * @param response The response to send.
	 */
	public void respondWithAction(String response) {
		if (null != channel) {
           channel.send().action(response);
        } else {
            user.send().action(response);
        }
	}
	
	@Override
	public T getBot() {
		return event.getBot();
	}

	@Override
	public long getTimestamp() {
		return event.getTimestamp();
	}

	@Override
	public int compareTo(Event<T> o) {
		return event.compareTo(o);
	}

	@Override
	public String getMessage() {
		return event.getMessage();
	}
}
