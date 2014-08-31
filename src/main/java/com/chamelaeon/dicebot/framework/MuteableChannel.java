/**
 * 
 */
package com.chamelaeon.dicebot.framework;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;

/**
 * A channel which can be muted.
 * @author Chamelaeon
 */
public class MuteableChannel extends Channel {
    /**
     * Constructor.
     * @param bot The bot this channel is for.
     * @param userChannelDao The user/channel DAO for the bot.
     * @param name The name of the channel.
     */
    public MuteableChannel(PircBotX bot, UserChannelDao<User, Channel> userChannelDao, String name) {
		super(bot, userChannelDao, name);
	}
	
	/**
	 * Whether this channel is muted or not.
	 * @return true if the channel is muted, false otherwise.
	 */
	public boolean isMuted() {
        return getOutputChannelSafely().isMuted();
    }

	/**
	 * Sets the muted value for this channel.
	 * @param muted True to mute the channel, false to unmute the channel.
	 */
    public void setMuted(boolean muted) {
	    getOutputChannelSafely().setMuted(muted);
	}
	
    /**
     * Helper method to ensure we can generate the output channel.
     * @return the muteable output channel.
     */
	private MuteableOutputChannel getOutputChannelSafely() {
    	try {
            return (MuteableOutputChannel) output.get();
        } catch (ConcurrentException ex) {
            throw new RuntimeException("Could not generate OutputChannel for " + getName(), ex);
        }
	}
}
