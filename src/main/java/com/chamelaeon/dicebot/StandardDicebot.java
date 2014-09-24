package com.chamelaeon.dicebot;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.exception.IrcException;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.statistics.StandardStatistics;

/** A bot whose purpose is to roll dice. */
public class StandardDicebot extends Dicebot {
	/** The object which keeps track of statistics. */
	private final StandardStatistics statistics;
	/** The object which maintains the "personality" of the bot. */
	private final Personality personality;
	/** The main run loop for the dicebot. */
	private final Executor mainLoop;
	
	/**
	 * Constructor.
	 * @param configBuilder Configuration builder for the dicebot.
	 * @param personality The personality for the bot.
	 */
	public StandardDicebot(Builder<Dicebot> configBuilder, Personality personality) {
		super(configBuilder.buildConfiguration());
        this.statistics = new StandardStatistics();
        this.personality = personality;
        this.mainLoop = Executors.newSingleThreadExecutor();
    }
	
	@Override
	public void start() throws IOException, IrcException {
		System.out.println("Attempting to connect to: " + getConfiguration().getServerHostname());
		
		// Now start our bot up.
		mainLoop.execute(new Runnable() {
			@Override
			public void run() {
				while(!reconnectStopped) {
					try {
						startBot();
					} catch (IOException | IrcException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
    @Override
    public StandardStatistics getStatistics() {
		return statistics;
	}
	
	@Override
    public Personality getPersonality() {
		return personality;
	}
	
     @Override
     protected void loggedIn(String nick) {
         // Do NOT set the nick if we already have one!
         if (!StringUtils.isEmpty(getNick())) {
             nick = getNick();
         }
         
         super.loggedIn(nick);
     }
	
	@Override
	public void disconnect() {
		shutdown(true);
	}
}
