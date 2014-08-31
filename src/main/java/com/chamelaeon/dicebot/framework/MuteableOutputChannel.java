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

    /**
     * Constructor.
     * @param bot The bot this channel output is for.
     * @param channel The channel output.
     */
    public MuteableOutputChannel(PircBotX bot, Channel channel) {
        super(bot, channel);
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
            super.message(message);
        }
    }

    @Override
    public void action(String action) {
        if (!muted) {
            super.action(action);
        }
    }
}
