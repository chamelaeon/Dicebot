package com.chamelaeon.dicebot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.IdentServer;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.IrcException;

import com.chamelaeon.dicebot.api.CardBase;
import com.chamelaeon.dicebot.api.Command;
import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.commands.CheatCommand;
import com.chamelaeon.dicebot.commands.DrawCardCommand;
import com.chamelaeon.dicebot.commands.HelpCommand;
import com.chamelaeon.dicebot.commands.JoinCommand;
import com.chamelaeon.dicebot.commands.LeaveCommand;
import com.chamelaeon.dicebot.commands.MuteUnmuteCommand;
import com.chamelaeon.dicebot.commands.StatusCommand;
import com.chamelaeon.dicebot.framework.DicebotBuilder;
import com.chamelaeon.dicebot.listener.NickGhostListener;
import com.chamelaeon.dicebot.listener.NickHandlingListener;
import com.chamelaeon.dicebot.listener.NickSetListener;
import com.chamelaeon.dicebot.listener.SendMotdListener;
import com.chamelaeon.dicebot.personality.PropertiesPersonality;
import com.chamelaeon.dicebot.rollers.FudgeRoller;
import com.chamelaeon.dicebot.rollers.L5RRoller;
import com.chamelaeon.dicebot.rollers.ShadowrunRoller;
import com.chamelaeon.dicebot.rollers.StandardRoller;
import com.chamelaeon.dicebot.rollers.WhiteWolfRoller;
import com.google.common.io.Closeables;

/**
 * Driver class for the dicebot.
 * @author Chamelaeon
 */
public class DicebotRunner {
    /** The list of help details for commands. */
	private final List<HelpDetails> commandHelpDetails;
	/** The list of help details for rollers. */
	private final List<HelpDetails> rollerHelpDetails;
	/** The dicebot's personality. */
	private Personality personality;
	/** The listener for handling nicks. */
    private NickHandlingListener nickListener;

    /** Constructor. */
	private DicebotRunner() {
		this.commandHelpDetails = new ArrayList<>();
		this.rollerHelpDetails = new ArrayList<>();
	}
	
	/**
	 * Runs the dicebot.
	 * <ul> 
	 * <li>The first argument should be a path to a properties file containing the props for the dicebot.
	 * <li>The second argument is optional but should be a path to a properties file containing the card database for the dicebot.
	 * </ul>
	 * @param args Arguments for the dicebot.  
	 */
	public static void main(String[] args) throws Exception {
		InputStream configStream;
		InputStream personalityStream;
		InputStream cardStream;
		if (args.length < 2) {
		    configStream = DicebotRunner.class.getResourceAsStream("/config.properties");
		    personalityStream = DicebotRunner.class.getResourceAsStream("/dicesuke.properties");
			cardStream = DicebotRunner.class.getResourceAsStream("/dramaCards.json");
			
			if (null == configStream || null == personalityStream) {
				System.out.println("This dicebot requires two arguments: a properties file with configuration options " 
				        + "and a properties file containing personality information. " 
						+ "An additional properties file with drama cards can be specified as well.");
				System.exit(1);
			}
		} else {
			// Find the arguments.
			String configPath = args[0];
			String personalityPath = args[1];
			String cardPath = null;
			if (args.length >= 3) {
				cardPath = args[2];
			}
			
			configStream = new FileInputStream(new File(configPath));
			personalityStream = new FileInputStream(new File(personalityPath));
			cardStream = new FileInputStream(new File(cardPath));
		}
		
		// Grab the configuration and personality properties.
		Properties configProps = new Properties();
		Properties personalityProps = new Properties();
		try {
			configProps.load(configStream);
			personalityProps.load(personalityStream);
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			Closeables.closeQuietly(configStream);
			Closeables.closeQuietly(personalityStream);
		}
		
		// Load the card base.
		CardBase cardBase = null;
		if (0 == cardStream.available()) {
    		try {
    		    cardBase = new CardBase(cardStream);
    		} catch (IOException ioe) {
    		    throw ioe;
            } finally {
                Closeables.closeQuietly(cardStream);
            }
		}
		
		DicebotRunner runner = new DicebotRunner();
		runner.start(configProps, personalityProps, cardBase);
	}
	
	/**
	 * Starts the dicebot. 
	 * @param config The configuration properties of the dicebot.
	 * @param personality The personality of the dicebot.
	 * @param cardBase The card base for the dicebot to use, if any.
	 * @throws IrcException if there is a problem with the bot framework.
	 * @throws IOException if there is a connection issue.
	 */
	private void start(Properties config, Properties personalityProps, CardBase cardBase) throws IrcException, IOException { 
		// Pull out properties we need.
		String network = config.getProperty("Network", "irc.sandwich.net");
		int port = Integer.parseInt(config.getProperty("Port", "6697"));
		boolean useSsl = Boolean.parseBoolean(config.getProperty("SSL", "true"));
		boolean trustAllCerts = Boolean.parseBoolean(config.getProperty("TrustAllCertificates", "false"));
		String nicks = config.getProperty("Nicks", "Dicebot");
		String nickservPassword = config.getProperty("NickservPassword", "");
		boolean useGhostIfNickExists = Boolean.parseBoolean(config.getProperty("UseGhostIfNickExists", "false"));
		String channels = config.getProperty("Channels");
		final String motd = config.getProperty("MotD");

		// Builder and mandatory config.
		this.personality =  new PropertiesPersonality(personalityProps, 
		                Boolean.parseBoolean(config.getProperty("UseCriticalSuccessMessages").trim()),
		                Boolean.parseBoolean(config.getProperty("UseCriticalFailureMessages").trim()));
		Builder<Dicebot> configBuilder = new DicebotBuilder(personality);
		configBuilder.setIdentServerEnabled(true);
		configBuilder.setAutoReconnect(true);
		configBuilder.setMaxLineLength(400);
		configBuilder.setRealName("DiceWombot");
		
		// Dynamic config.
		configBuilder.setServer(network, port);
		configBuilder.setNickservPassword(nickservPassword);
		
		// SSL config.
		if (useSsl) {
			UtilSSLSocketFactory socketFactory = new UtilSSLSocketFactory();
			if (trustAllCerts) {
				socketFactory = socketFactory.trustAllCertificates();
			}
			configBuilder.setSocketFactory(socketFactory);
		}
		
        processNicks(nicks, useGhostIfNickExists, nickservPassword, configBuilder);
        processChannels(channels, configBuilder);
        createRollers(configBuilder);
        createCommands(configBuilder, cardBase);
        configBuilder.addListener(new SendMotdListener(motd));
        
        // Start the ident server before anything else, unless there's already one running.
        try {
        	IdentServer.startServer();
        } catch (Exception e) {
        	configBuilder.setIdentServerEnabled(false);
        }
        Security.addProvider(new BouncyCastleProvider());
        Dicebot bot = new StandardDicebot(configBuilder, personality);
        bot.start();

        // Listen for command-line input.
        Scanner scanner = null;
        try {
        	scanner = new Scanner(System.in);
	        scanner.useDelimiter("\\n");
	        while (scanner.hasNextLine()) {
	        	String line = "";
	        	try { 
		        	line = scanner.nextLine();
		        	
		        	if (line.equals("exit") || line.equals("quit")) {
		        		bot.disconnect();
		        		System.exit(0);
		        	}
		        	
		        	// Allow the person running it to reset the nicks
		        	if (line.equals("nickreset")) {
		        	    nickListener.resetNickIndex();
		        	    bot.disconnect();
		        	    continue;
		        	}
		        	
		        	// Allow for raw command sending.
		        	if (line.startsWith("//")) {
		        	    line = line.substring(2);
		        	    bot.sendRaw().rawLine(line);
		        	    continue;
		        	}
		        	
		        	String[] parts = line.split(" ");
		        	String channel = parts[0];
		        	if (!channel.startsWith("#")) {
		        	    channel = "#" + channel;
		        	}
		        	
		        	if (parts[1].equals("/me")) {
		        		String message = line.substring(channel.length() + 4).trim();
		        		bot.getUserChannelDao().getChannel(channel).send().action(message);
		        	} else {
		        		String message = line.substring(channel.length()).trim();
		        		bot.getUserChannelDao().getChannel(channel).send().message(message);
		        	}
	        	} catch (Exception e) {
	        		System.out.println("Encountered invalid input " + line + "! (Exception type was: " + e.getClass() + ", message was: " + e.getMessage() + ")");
	        	}
	        }
        } finally {
    		Closeables.close(scanner, false);
    	}
	}

	/**
	 * Creates the commands for the dicebot.
	 * @param configBuilder The configuration Builder to register commands with.
	 * @param cardPath The card path to use.
	 */
	private void createCommands(Builder<Dicebot> configBuilder, CardBase cardBase) {
		registerCommand(new MuteUnmuteCommand(), configBuilder);
		registerCommand(new LeaveCommand(), configBuilder);
		registerCommand(new StatusCommand(), configBuilder);
		registerCommand(new JoinCommand(), configBuilder);
		registerCommand(new CheatCommand(), configBuilder);
		if (null != cardBase) {
			registerCommand(new DrawCardCommand(cardBase), configBuilder);
		}
		
		// Always register the help command last so it has all the help details.
		registerCommand(new HelpCommand(commandHelpDetails, rollerHelpDetails), configBuilder);
	}
	
	/**
	 * Registers a command with the configuration builder and help systems.
	 * @param command The command to register.
	 * @param configBuilder The configuration Builder to register commands with.
	 */
	private void registerCommand(Command command, Builder<Dicebot> configBuilder) {
		configBuilder.addListener(command);
		commandHelpDetails.add(command.getHelpDetails());
	}
	
	/**
	 * Processes all channels that should be joined initially.
	 * @param channelsString The channels that should be joined, in a comma-delimited string.
	 * @param configBuilder The config builder.
	 */
	private void processChannels(String channelsString, Builder<Dicebot> configBuilder) {
		String[] channels = channelsString.split(",");
		for (String channel : channels) {
            configBuilder.addAutoJoinChannel(channel);
        }
	}
	
	/**
	 * Processes all nicks for use by the bot. If useGhostIfNickExists is false, the bot will attempt to use
	 * alternate provided nicks if the first one is in use. If true, the bot will attempt to use the nickserv
	 * "ghost" command to kick the nick and reclaim it.
	 * 
	 * @param nickString The bot's nicks, in a comma-delimited string.
	 * @param useGhostIfNickExists If true, will use the ghost strategy for nick in use problems.
	 *                             If false, will use the alternate nick strategy instead.
	 * @param nickservPassword The nickserv password to use for ghosting.
	 * @param configBuilder The config builder.
	 */
	private void processNicks(String nickString, boolean useGhostIfNickExists, String nickservPassword, 
	        Builder<Dicebot> configBuilder) {
		String[] nicks = nickString.split(",");
		
		if (useGhostIfNickExists) {
		    nickListener = new NickGhostListener(nickservPassword);
		} else {
		    nickListener = new NickSetListener(nicks);
		}
		configBuilder.addListener(nickListener);
		// Set the first nick.
		configBuilder.setName(nicks[0]);
	}
	
	/**
	 * Creates the rollers for the dicebot.
	 * @param configBuilder The configuration Builder to register rollers with.
	 */
	private void createRollers(Builder<Dicebot> configBuilder) {
		registerRoller(new StandardRoller(personality), configBuilder);
		registerRoller(new L5RRoller(personality), configBuilder);
		registerRoller(new WhiteWolfRoller(personality), configBuilder);
		registerRoller(new FudgeRoller(personality), configBuilder);
		registerRoller(new ShadowrunRoller(personality), configBuilder);
	}
	
	/**
     * Registers a roller with the configuration builder and help systems.
     * @param roller The roller to register.
     * @param configBuilder The configuration Builder to register rollers with.
     */
	private void registerRoller(Command roller, Builder<Dicebot> configBuilder) {
		configBuilder.addListener(roller);
		rollerHelpDetails.add(roller.getHelpDetails());
	}
}
