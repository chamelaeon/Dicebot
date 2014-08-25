/**
 * 
 */
package com.chamelaeon.dicebot.listener;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;

import com.chamelaeon.dicebot.Dicebot;

/**
 * A listener that joins channels upon connection to the server. This also functions as auto-reconnect for those channels.
 * @author Chamelaeon
 */
public class ChannelJoinListener extends ListenerAdapter<Dicebot> {
    /** The channels to join. */
	private String[] channels;

	/**
	 * Constructor.
	 * @param channels The channels to join.
	 */
	public ChannelJoinListener(String[] channels) {
		this.channels = channels;
	}

	@Override
	public void onConnect(ConnectEvent<Dicebot> event) throws Exception {
		for (String channel : channels) {
		    event.getBot().sendIRC().joinChannel(channel);
		}
	}
}
