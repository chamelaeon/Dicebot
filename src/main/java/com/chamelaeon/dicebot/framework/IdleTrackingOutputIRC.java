/**
 * 
 */
package com.chamelaeon.dicebot.framework;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputIRC;

import com.chamelaeon.dicebot.IdleChannelTracker;

/**
 * OutputIRC class that tracks the joining of, messages to, and actions to channels.
 * @author Chamelaeon
 */
public class IdleTrackingOutputIRC extends OutputIRC {
    /** The object that tracks whether a channel is idle or not. */
    private final IdleChannelTracker idleTracker;

    /**
     * Constructor.
     * @param idleTracker The object that tracks whether a channel is idle or not.
     * @param bot The bot.
     */
    public IdleTrackingOutputIRC(IdleChannelTracker idleTracker, PircBotX bot) {
        super(bot);
        this.idleTracker = idleTracker;
    }

    @Override
    public void joinChannel(String channel, String key) {
        //Overriding this to prevent the extra space at the end of the channel name.
        checkArgument(StringUtils.isNotBlank(channel), "Channel '%s' is blank", channel);
        checkNotNull(key, "Key for channel %s cannot be null", channel);
        
        if (!StringUtils.isBlank(key)) {
            channel = channel + " " + key;
        }
        joinChannel(channel);
    }

    @Override
    public void joinChannel(String channel) {
        Channel channelObj = bot.getUserChannelDao().getChannel(channel);
        if (idleTracker.addChannel(channelObj)) {
            super.joinChannel(channel);
        } else {
            throw new IllegalStateException("Max channels!");
        }
    }
}
