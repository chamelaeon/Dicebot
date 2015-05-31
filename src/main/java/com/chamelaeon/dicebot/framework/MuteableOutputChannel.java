/**
 * 
 */
package com.chamelaeon.dicebot.framework;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputChannel;


/**
 * An output channel that can be muted.
 * @author Chamelaeon
 */
public class MuteableOutputChannel extends OutputChannel {
    /** Whether the output channel is muted or not. */
    private boolean muted;
    /** The object that tracks whether a channel is idle or not. */
    private final IdleChannelTracker idleTracker;

    /**
     * Constructor.
     * @param bot The bot this channel output is for.
     * @param channel The channel output.
     * @param idleTracker The tracker for tracking idle channels.
     */
    public MuteableOutputChannel(PircBotX bot, Channel channel, IdleChannelTracker idleTracker) {
        super(bot, channel);
        this.idleTracker = idleTracker;
        this.muted = false;
    }

    /**
     * Whether this channel output is muted or not.
     * @return true if the channel output is muted, false otherwise.
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * Sets the muted value for this channel output
     * @param muted True to mute the channel output, false to unmute the channel output.
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    @Override
    public void message(String message) {
        if (!muted) {
            idleTracker.updateChannelIdle(channel);
            super.message(message);
        }
    }

    @Override
    public void action(String action) {
        if (!muted) {
            idleTracker.updateChannelIdle(channel);
            super.action(action);
        }
    }

    @Override
    public void part() {
        idleTracker.removeChannel(channel);
        super.part();
    }

    @Override
    public void part(String reason) {
        idleTracker.removeChannel(channel);
        super.part(reason);
    }
}
