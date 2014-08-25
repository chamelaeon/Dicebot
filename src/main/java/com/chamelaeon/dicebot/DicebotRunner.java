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
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.UserListEvent;

import com.chamelaeon.dicebot.commands.CheatCommand;
import com.chamelaeon.dicebot.commands.Command;
import com.chamelaeon.dicebot.commands.DrawCardCommand;
import com.chamelaeon.dicebot.commands.HelpCommand;
import com.chamelaeon.dicebot.commands.HelpDetails;
import com.chamelaeon.dicebot.commands.JoinCommand;
import com.chamelaeon.dicebot.commands.LeaveCommand;
import com.chamelaeon.dicebot.commands.MuteUnmuteCommand;
import com.chamelaeon.dicebot.commands.StatusCommand;
import com.chamelaeon.dicebot.listener.ChannelJoinListener;
import com.chamelaeon.dicebot.listener.NickSetListener;
import com.chamelaeon.dicebot.personality.PropertiesPersonality;
import com.chamelaeon.dicebot.rollers.Roller.FudgeRoller;
import com.chamelaeon.dicebot.rollers.Roller.L5RRoller;
import com.chamelaeon.dicebot.rollers.Roller.StandardRoller;
import com.chamelaeon.dicebot.rollers.Roller.WhiteWolfRoller;
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
	private PropertiesPersonality personality;
	/** The statistics for the dicebot. */
	private Statistics statistics;
	
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
		if (args.length < 1) {
			System.out.println("This dicebot requires a single argument: a properties file containing personality information. " 
					+ "An additional properties file with drama cards can be specified as well.");
			System.exit(1);
		} 
		
		// Find the arguments.
		String fileName = args[0];
		String cardPath = null;
		if (args.length >= 2) {
			cardPath = args[1];
		}
		// Grab the properties.
		Properties props = new Properties();
		InputStream propStream = null;
		try {
			propStream = new FileInputStream(new File(fileName));
			props.load(propStream);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			Closeables.closeQuietly(propStream);
		}
		
		DicebotRunner runner = new DicebotRunner();
		runner.start(props, cardPath);
	}
	
	/**
	 * Starts the dicebot. 
	 * @param props The properties of the dicebot.
	 * @param cardPath The card path for the dicebot to use, if any.
	 * @throws IrcException if there is a problem with the bot framework.
	 * @throws IOException if there is a connection issue.
	 */
	private void start(Properties props, String cardPath) throws IrcException, IOException { 
		// Pull out properties we need.
		String network = props.getProperty("Network", "irc.sandwich.net");
		int port = Integer.parseInt(props.getProperty("Port", "6697"));
		boolean useSsl = Boolean.parseBoolean(props.getProperty("SSL", "true"));
		boolean trustAllCerts = Boolean.parseBoolean(props.getProperty("TrustAllCertificates", "false"));
		String nicks = props.getProperty("Nicks", "Dicebot");
		String nickservPassword = props.getProperty("NickservPassword", "");
		String channels = props.getProperty("Channels");

		// Builder and mandatory config.
		this.personality = new PropertiesPersonality(props, cardPath);
	    this.statistics = new Statistics();
		Builder<Dicebot> configBuilder = new DicebotBuilder();
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
		
        processNicks(nicks, configBuilder);
        processChannels(channels, configBuilder);
        createRollers(configBuilder);
        createCommands(configBuilder);

        configBuilder.addListener(new ListenerAdapter<Dicebot>() {
			@Override
			public void onUserList(UserListEvent<Dicebot> event) throws Exception {
				event.getChannel().send().message("Hey. I'm in alpha at the moment, everything is a little unstable. Tell Chamelaeon if something breaks.");
			}
		});
        
        // Start the ident server before anything else, unless there's already one running.
        try {
        	IdentServer.startServer();
        } catch (Exception e) {
        	configBuilder.setIdentServerEnabled(false);
        }
        Security.addProvider(new BouncyCastleProvider());
        Dicebot bot = new Dicebot(configBuilder, personality, statistics);
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
		        	String[] parts = line.split(" ");
		        	String channel = parts[0];
		        	
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
    		Closeables.closeQuietly(scanner);
    	}
	}

	/**
	 * Creates the commands for the dicebot.
	 * @param configBuilder The configuration Builder to register commands with.
	 */
	private void createCommands(Builder<Dicebot> configBuilder) {
		registerCommand(new MuteUnmuteCommand(), configBuilder);
		registerCommand(new LeaveCommand(), configBuilder);
		registerCommand(new StatusCommand(), configBuilder);
		registerCommand(new JoinCommand(), configBuilder);
		registerCommand(new CheatCommand(), configBuilder);
		if (null != personality.getCardPath()) {
			registerCommand(
					new DrawCardCommand(new CardBase(personality.getCardPath())), configBuilder);
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
		configBuilder.addListener(new ChannelJoinListener(channels));
	}
	
	/**
	 * Processes all nicks for use by the bot.
	 * @param nickString The bot's nicks, in a comma-delimited string.
	 * @param configBuilder The config builder.
	 */
	private void processNicks(String nickString, Builder<Dicebot> configBuilder) {
		String[] nicks = nickString.split(",");
		configBuilder.addListener(new NickSetListener(nicks));
		// Set the first nick.
		configBuilder.setName(nicks[0]);
	}
	
	/**
	 * Creates the rollers for the dicebot.
	 * @param configBuilder The configuration Builder to register rollers with.
	 */
	private void createRollers(Builder<Dicebot> configBuilder) {
		registerRoller(new StandardRoller(statistics, personality), configBuilder);
		registerRoller(new L5RRoller(statistics, personality), configBuilder);
		registerRoller(new WhiteWolfRoller(statistics, personality), configBuilder);
		registerRoller(new FudgeRoller(statistics, personality), configBuilder);
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
