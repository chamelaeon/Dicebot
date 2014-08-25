/**
 * 
 */
package com.chamelaeon.dicebot;

import org.pircbotx.Channel;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputChannel;

/**
 * A custom configuration builder.
 * @author Chamelaeon
 */
public class DicebotBuilder extends Builder<Dicebot> {

    @Override
    public BotFactory getBotFactory() {
        return new DicebotFactory();
    }

    /** Factory for providing custom classes inside the PircBotX framework.  */
    private class DicebotFactory extends BotFactory {
        @Override
        public Channel createChannel(PircBotX bot, String name) {
            return new MuteableChannel(bot, bot.getUserChannelDao(), name);
        }

        @Override
        public OutputChannel createOutputChannel(PircBotX bot, Channel channel) {
            return new MuteableOutputChannel(bot, channel);
        }
    }
}
