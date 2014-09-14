/**
 * 
 */
package com.chamelaeon.dicebot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

/**
 * Class which tracks what channels the user is in, and how idle those channels are.
 * @author Chamelaeon
 */
public class IdleChannelTracker {
    /** A map of channels to their last used time. */
    private final Map<Channel, Long> channelLastUsedTime;
    /** The list of "permanent" channels, i.e. channels the bot will always join and never leave. */
    private final List<String> permanentChannels;
    /** The limit to the number of channels the bot can be in. */
    private int channelLimit;
    
    /** Constructor. */
    public IdleChannelTracker() {
        channelLastUsedTime = new HashMap<>();
        permanentChannels = new ArrayList<>();
    }

    /**
     * Updates the idle status of a channel.
     * @param channel The channel to update.
     */
    public void updateChannelIdle(Channel channel) {
        // Can't believe I have to do this, but there's an errant space at the end of the channel name.
        if (permanentChannels.contains(channel.getName().trim())) {
            if (!channelLastUsedTime.containsKey(channel)) {
                channelLastUsedTime.put(channel, Long.MAX_VALUE);
            }
        } else {
            channelLastUsedTime.put(channel, System.currentTimeMillis());
        }
    }
    
    /**
     * Gets the channel the bot is in with the longest idle time. If the bot is in no channels, returns null.
     * @return the most idle channel.
     */
    public Channel getMostIdleChannel() {
        Comparator<Channel> timeComparator = Ordering.natural().onResultOf(Functions.forMap(channelLastUsedTime));
         Map<Channel, Long> map = ImmutableSortedMap.copyOf(channelLastUsedTime, timeComparator);
         
         if (map.isEmpty()) {
             return null;
         } else {
             return map.entrySet().iterator().next().getKey();
         }
    }
    
    /**
     * Adds a channel to idle tracking. Generally this means the bot is in the channel. If the bot
     * cannot join the channel because it is at maximum, it will try to find the channel which has been
     * idle the longest. If it cannot find a non-permanent channel that has not been idle at least one
     * hour, it will return false. If it can find an idle channel to leave, or if it has channel space
     * free, the channel will be added to tracking and this method will return true.
     * 
     * @param channel The channel to join.
     * @return true if the channel should be joined, false if it cannot be due to channel limits.
     */
    public boolean addChannel(Channel channel) {
        // If it's a permanent channel or already in the map, skip it but return true.
        if (permanentChannels.contains(channel.getName()) || channelLastUsedTime.containsKey(channel)) {
            return true;
        } else if (channelLastUsedTime.size() == channelLimit) {
            // We're at maximum channels. Can we get rid of one?
            Channel idleCandidate = getMostIdleChannel();
            
            // Find the most idle channel and how long it's been idle.
            long idleTime = channelLastUsedTime.get(idleCandidate);
            long now = System.currentTimeMillis();
            
            // TODO: Jodatime. This is checking if it's at least 1h idle.
            if (now - idleTime <= 1000 * 60 * 60 * 1) {
                // TODO: Message and personality.
                idleCandidate.send().part("See you suckers, there's action elsewhere.");
                updateChannelIdle(channel);
                return true;
            } else {
                return false;
            }
        } else {
            // Normal update.
            updateChannelIdle(channel);
            return true;
        }
    }
    
    /**
     * Adds a permanent channel to the bot. The bot will never consider this channel to be
     * eligible to be most idle.
     * @param channel The channel name to add.
     */
    public void addPermanentChannel(String channel) {
        permanentChannels.add(channel);
    }

    /** 
     * Removes a channel from idle tracking status. Generally this means the bot is no longer 
     * in that channel.
     * @param channel The channel to remove.
     */
    public void removeChannel(Channel channel) {
        channelLastUsedTime.remove(channel);
    }
    
    /**
     * Parses and sets the channel limit for the server.
     * @param channelLimit The channel limit string from ServerInfo.
     */
    public void setChannelLimit(String channelLimit) {
        // Account for charybdis et al. 
        channelLimit = channelLimit.replace("#", "");
        channelLimit = channelLimit.replace("&", "");
        channelLimit = channelLimit.replace(":", "");
        this.channelLimit = Integer.parseInt(channelLimit);
    }
    
    /**
     * Returns the current channel limit.
     * @return the channel limit. 
     */
    public int getChannelLimit() {
        return this.channelLimit;
    }
}
