package com.chamelaeon.dicebot.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PartEvent;

import com.chamelaeon.dicebot.api.Dicebot;

/**
 * Listener which keeps track of when channels are joined, and on the first time they're joined,
 * sends a MotD.  
 * @author Chamelaeon
 */
public class SendMotdListener extends ListenerAdapter<Dicebot> {
    /** The MotD to send. */
    private final String motd;
    /** The list of channels that have been seen. */
    private final List<String> seenChannels;

    /**
     * Constructor.
     * @param motd The MotD to send.
     */
    public SendMotdListener(String motd) {
        this.motd = motd;
        seenChannels = new ArrayList<>();
    }

    @Override
    public void onJoin(JoinEvent<Dicebot> event) throws Exception {
        // If it was the bot who joined...
        if (event.getBot().getNick().equals(event.getUser().getNick())) {
            String channelName = event.getChannel().getName();
            if (!StringUtils.isEmpty(motd) && !seenChannels.contains(channelName)) {
                event.getChannel().send().message(motd);
                seenChannels.add(channelName);
            }
        }
    }

    @Override
    public void onPart(PartEvent<Dicebot> event) throws Exception {
        // If it was the bot who parted...
        if (event.getBot().getNick().equals(event.getUser().getNick())) {
            seenChannels.remove(event.getChannel().getName());
        }
    }
}