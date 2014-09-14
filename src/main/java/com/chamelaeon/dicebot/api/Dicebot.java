package com.chamelaeon.dicebot.api;

import java.io.IOException;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;



/**
 * "Wrapper" class to hand around instead of the actual dicebot class, to avoid package cycles.
 * @author Chamelaeon
 */
public abstract class Dicebot extends PircBotX {

    /**
     * Constructor.
     * @param configuration The configuration.
     */
    public Dicebot(Configuration<? extends PircBotX> configuration) {
        super(configuration);
    }

    /**
     * Starts the dicebot.
     * @throws IOException if the connection fails due to IO issues.
     * @throws IrcException if the server will not let us connect. 
     */
    public abstract void start() throws IOException, IrcException;
    
    /**
     * Returns the statistics object for the dicebot.
     * @return the statistics.
     */
    public abstract Statistics getStatistics();

    /**
     * Returns the personality object for the dicebot.
     * @return the personality.
     */
    public abstract Personality getPersonality();
    
    /** Disconnects the bot from the server and stops it. */
    public abstract void disconnect();
}