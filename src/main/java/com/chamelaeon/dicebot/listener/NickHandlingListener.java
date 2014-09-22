package com.chamelaeon.dicebot.listener;

import org.pircbotx.hooks.Listener;

import com.chamelaeon.dicebot.api.Dicebot;

public interface NickHandlingListener extends Listener<Dicebot>{

    /**
     * Resets the nick index, allowing the bot to start back at the beginning of its nicklist and 
     * potentially reclaim a nick that has pinged out. 
     */
    public abstract void resetNickIndex();

}