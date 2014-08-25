package com.chamelaeon.dicebot;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import com.chamelaeon.dicebot.personality.Personality;

/** A bot whose purpose is to roll dice. */
public class Dicebot extends PircBotX {
	/** The object which keeps track of statistics. */
	private final Statistics statistics;
	/** The object which maintains the "personality" of the bot. */
	private final Personality personality;
	/** The main run loop for the dicebot. */
	private final Executor mainLoop;
	
	/**
	 * Constructor.
	 * @param configBuilder Configuration builder for the dicebot.
	 * @param personality The personality for the bot.
	 * @param statistics The statistics for the bot.
	 */
	public Dicebot(Builder<Dicebot> configBuilder, Personality personality, Statistics statistics) {
		super(configBuilder.buildConfiguration());
        this.statistics = statistics;
        this.personality = personality;
        this.mainLoop = Executors.newSingleThreadExecutor();
    }
	
	/**
	 * Starts the dicebot.
	 * @throws IOException if the connection fails due to IO issues.
	 * @throws IrcException if the server will not let us connect. 
	 */
	public void start() throws IOException, IrcException {
		System.out.println("Attempting to connect to: " + getConfiguration().getServerHostname());
		
		// Now start our bot up.
		mainLoop.execute(new Runnable() {
			@Override
			public void run() {
				// TODO: HA HA HA NO
				while(true) {
					try {
						startBot();
					} catch (IOException | IrcException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	/**
     * Joins the given channel.
     * @param channel The channel to join.
     */
	public void joinChannel(String channel) {
	    System.out.println("Joining channel: " + channel);
        sendIRC().joinChannel(channel);
	}
	
	/**
	 * Returns the statistics object for the dicebot.
	 * @return the statistics.
	 */
	public Statistics getStatistics() {
		return statistics;
	}
	
	/**
	 * Returns the personality object for the dicebot.
	 * @return the personality.
	 */
	public Personality getPersonality() {
		return personality;
	}
	
	/** Disconnects the bot from the server and stops it. */
	public void disconnect() {
		stopBotReconnect();
		// TODO: Better message and personality extract.
		sendIRC().quitServer("Someone quit me from the command line.");
	}
}
