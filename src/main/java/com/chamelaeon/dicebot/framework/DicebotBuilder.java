/**
 * 
 */
package com.chamelaeon.dicebot.framework;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputIRC;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.Personality;

/**
 * A custom configuration builder.
 * @author Chamelaeon
 */
public class DicebotBuilder extends Builder<Dicebot> {

    /** Object for tracking the most idle channel. */
    private final IdleChannelTracker idleTracker;
    private final DicebotFactory botFactory;
    
    /** 
     * Constructor.
     * @param personality The personality for the bot. 
     */
    public DicebotBuilder(Personality personality) {
        this.idleTracker = new IdleChannelTracker(personality);
        this.botFactory = new DicebotFactory(idleTracker);
        
        this.addListener(new ListenerAdapter<Dicebot>() {
            @Override
            public void onServerResponse(ServerResponseEvent<Dicebot> event) throws Exception {
                String chanLimit = event.getBot().getServerInfo().getChanlimit();
                if (!StringUtils.isBlank(chanLimit) && 0 == idleTracker.getChannelLimit()) {
                    idleTracker.setChannelLimit(chanLimit);
                }
            }
        });
    }
    
    @Override
    public BotFactory getBotFactory() {
        return botFactory;
    }

    /** Factory for providing custom classes inside the PircBotX framework.  */
    private class DicebotFactory extends BotFactory {
        /** Object for tracking the most idle channel. */
        private final IdleChannelTracker idleTracker;
        
        /** 
         * Constructor.
         * @param idleTracker The idle tracker. 
         */
        public DicebotFactory(IdleChannelTracker idleTracker) {
            this.idleTracker = idleTracker;
        }
        
        @Override
        public Channel createChannel(PircBotX bot, String name) {
            return new MuteableChannel(bot, bot.getUserChannelDao(), name);
        }

        @Override
        public OutputChannel createOutputChannel(PircBotX bot, Channel channel) {
            return new MuteableOutputChannel(bot, channel, idleTracker);
        }

        @Override
        public OutputIRC createOutputIRC(PircBotX bot) {
            /* This only happens once, so it's the best place to fill the idle
            tracker with all the "permanent" channels. */
            for (String channel : bot.getConfiguration().getAutoJoinChannels().keySet()) {
                idleTracker.addPermanentChannel(channel);
            }
            
            return new IdleTrackingOutputIRC(idleTracker, bot);
        }
    }
}
