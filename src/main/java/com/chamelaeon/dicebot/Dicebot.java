package com.chamelaeon.dicebot;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.chamelaeon.dicebot.commands.CheatCommand;
import com.chamelaeon.dicebot.commands.Command;
import com.chamelaeon.dicebot.commands.DrawCardCommand;
import com.chamelaeon.dicebot.commands.JoinCommand;
import com.chamelaeon.dicebot.commands.LeaveCommand;
import com.chamelaeon.dicebot.commands.MuteCommand;
import com.chamelaeon.dicebot.commands.StatusCommand;
import com.chamelaeon.dicebot.commands.UnmuteCommand;
import com.chamelaeon.dicebot.personality.Personality;
import com.chamelaeon.dicebot.rollers.Roller;
import com.chamelaeon.dicebot.rollers.Roller.FudgeRoller;
import com.chamelaeon.dicebot.rollers.Roller.L5RRoller;
import com.chamelaeon.dicebot.rollers.Roller.StandardRoller;
import com.chamelaeon.dicebot.rollers.Roller.WhiteWolfRoller;
import com.google.common.base.Joiner;

/** A {@link PircBot} whose purpose is to roll dice. */
public class Dicebot extends PircBot {
	/** The rollers the dicebot can use. */
	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Roller>> ROLLER_CLASSES = Arrays.asList(StandardRoller.class, L5RRoller.class, 
			WhiteWolfRoller.class, FudgeRoller.class);
	
	/** The commands the dicebot can follow, mapped to their string versions. */
	private final Map<String, Command> commands;
	/** The rollers the dicebot can use, mapped to their string versions. */
	private final Map<String, Roller> rollers;
	/** The consumers, indexed by the patterns to match against. */
	private final Map<Pattern, LineConsumer> consumers;
	/** The list of nicks for the dicebot to use. The 0 index is the primary, the others are alternates to use in order. */
	private final List<String> nicks;
	/** The channels for the dicebot to join, mapped to whether it is active in that channel or not. */
	private final Map<String, Boolean> channels;
	/** The IRC server to connect to. */
	private final String server;
	/** The retry count for the dicebot. */
	private final int retryCount = 5;
	/** The object which keeps track of statistics. */
	private final Statistics statistics;
	/** The object which maintains the "personality" of the bot. */
	private final Personality personality;
	
	/**
	 * Creates a new dicebot instance.
	 * @param server The URL string of the IRC server to connect to.
	 * @param nicks The list of nicks for the dicebot to use. The 0 index is the primary, the others are alternates to use in order.
	 */
	public Dicebot(String server, Personality personality, List<String> nicks, List<String> channels) {
        this.nicks = nicks;
        this.server = server;
        this.statistics = new Statistics();
        this.personality = personality;
        this.rollers = createRollerMap();
        this.commands = createCommandMap();
        this.consumers = createPatternMap();
        this.channels = new HashMap<String, Boolean>();
        for (String channel : channels) {
			this.channels.put(channel, true);
		}
        this.setMessageDelay(250);
    }
	
	/**
	 * Creates the map of commands the bot understands.
	 * @return the command map.
	 */
	private Map<String, Command> createCommandMap() {
		Map<String, Command> commands = new HashMap<String, Command>();
		commands.put("mute", new MuteCommand());
		commands.put("unmute", new UnmuteCommand());
		commands.put("leave", new LeaveCommand());
		commands.put("help", new HelpCommand());
		commands.put("status", new StatusCommand());
		commands.put("join", new JoinCommand());
		commands.put("cheat", new CheatCommand());
		if (null != personality.getCardPath()) {
			commands.put("draw", new DrawCardCommand(new CardBase(personality.getCardPath())));
		}
		return commands;
	}
	
	private Map<String, Roller> createRollerMap() {
		Map<String, Roller> rollers = new HashMap<String, Roller>();
		try {
			for (Class<? extends Roller> clazz : ROLLER_CLASSES) {
				Constructor<? extends Roller> constructor = clazz.getConstructor(Statistics.class, Personality.class);
				Roller roller = constructor.newInstance(statistics, personality);
				rollers.put(roller.getName(), roller);
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		
		return rollers;
	}
	
	/** Shorthand method to create the map of patterns to line consumers. */
	private Map<Pattern, LineConsumer> createPatternMap() {
		Map<Pattern, LineConsumer> patterns = new HashMap<Pattern, LineConsumer>();
		for (Roller roller : rollers.values()) {
			patterns.put(Pattern.compile(roller.getRegexp()), roller);
		}
		
		for (Command command : commands.values()) {
			patterns.put(Pattern.compile("^!" + command.getRegexp()), new CommandConsumer(command));
		}
		return patterns;
	}
	
	/**
	 * Starts the dicebot.
	 * @throws IOException if the connection fails due to IO issues.
	 * @throws IrcException if the server will not let us connect. 
	 */
	public void start() throws IOException, IrcException {
		System.out.println("Attempting to connect to: " + server);
		doConnect();
		joinChannels(channels.keySet());
	}
	
	@Override
	protected void onDisconnect() {
		int i = 0;		
		while (i < retryCount) {
			try {
				System.out.println("Attempting to reconnect to: " + server);
				doConnect();
				joinChannels(channels.keySet());
				return;
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage());
			} catch (IrcException irce) {
				System.err.println(irce.getMessage());
			}
		}
		System.out.println("Exceeded the number of retries! Sitting here and doing nothing...");
	}
	
	/**
	 * Perform the actual connect. 
	 * @throws IOException if the connection fails due to IO issues.
	 * @throws IrcException if the server will not let us connect.
	 */
	private void doConnect() throws IOException, IrcException {
		int i = 0;
		while (i < nicks.size()) {
			try {
				this.setName(nicks.get(i));
		        connect(server);
		        System.out.println("Connected!");
		        return;
			} catch (NickAlreadyInUseException naiue) {
				if (i + 1 != nicks.size()) {
					System.out.println(nicks.get(i) + " already in use. Retrying with " + nicks.get(++i) + ".");
				} else {
					throw new IrcException("All given nicks are currently being used!");
				}
			}
		} 
	}
	
	/**
	 * Join each of the channels in the collection, regardless of listening status or not.
	 * @param channels The channels to join.
	 */
	private void joinChannels(Collection<String> channels) {
		System.out.println("Joining channels: " + channels);
        for (String channel : channels) {
        	joinChannel(channel);
		}
	}
	
	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {		
		handleMessage(sender, message, sender);
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		handleMessage(channel, message, sender);
	}

	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
		channels.put(channel, true);
		joinChannel(channel);
	}
	
	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, 
			String recipientNick, String reason) {
		channels.remove(channel);
	}

	/**
	 * Handles incoming messages.
	 * @param destination The destination that responses should be sent to.
	 * @param message The incoming message.
	 * @param user The user who initiated the roll.
	 */
	private void handleMessage(String destination, String message, String user) {
		if (null != message) {
			message = message.trim();
		}
		
		for (Entry<Pattern, LineConsumer> entry : consumers.entrySet()) {
			Matcher m = entry.getKey().matcher(message);
			boolean matched = m.matches();
			if (matched) {
				System.out.println("Matched against: " + entry.getKey());
				boolean canSpeak = (channels.get(destination) == null ? true : channels.get(destination));
				try {
					String returnValue = entry.getValue().consume(m, destination, user);
					if (canSpeak && null != returnValue) {
						sendAction(destination, returnValue);
					}
				} catch (InputException ie) {
					// Just echo our error back to the user.
					if (canSpeak) {
						sendMessage(destination, ie.getMessage());
					} else {
						partChannel(destination, "BRB, running off to dump core!");
					}
				}
				break;
			}
		}
	}
	
	/**
	 * Adds a channel for the dicebot to pay attention to. 
	 * @param channel The channel to add.
	 */
	public void addChannel(String channel) {
		channels.put(channel, false);
	}
	
	/**
	 * Removes a channel from the dicebot's tracking.
	 * @param channel The channel to remove.
	 */
	public void removeChannel(String channel) {
		channels.remove(channel);
	}
	
	/**
	 * Returns the speaking status of the channel for the dicebot. False means the dicebot is muted for that channel.
	 * Null means the dicebot is not tracking that channel. 
	 * @param channel The channel to mute for.
	 * @return false if muted, true if not, null if not tracking.
	 */
	public Boolean getChannelStatus(String channel) {
		return channels.get(channel);
	}
	
	/**
	 * Sets the speaking status of the channel.
	 * @param channel The channel to check.
	 * @param state The speaking status to set.
	 */
	public void setChannelState(String channel, Boolean state) {
		channels.put(channel, state);
	}
	
	/**
	 * Returns the number of channels the dicebot is tracking.
	 * @return the channel count.
	 */
	public int getChannelCount() {
		return channels.size();
	}
	
	/**
	 * Returns the dicebot status for showing to the user.
	 * @return the status.
	 */
	public String getStatus() {
		return personality.getStatus();
	}
	
	/**
	 * Returns the statistics object for the dicebot.
	 * @return the statistics.
	 */
	public Statistics getStatistics() {
		return statistics;
	}
	
	/**
	 * Returns a personality message with the given key.
	 * @param key The key to get the message for.
	 * @return the message.
	 */
	public String getPersonalityMessage(String key) {
		return personality.getMessage(key);
	}

	/** Executes commands from either a channel/private message, or the command line. */
	private class CommandConsumer implements LineConsumer {
		/** The command to dispatch to. */
		private final Command command;
		
		/** 
		 * Dispatches to a command.
		 * @param command The command to dispatch to.
		 */
		public CommandConsumer(Command command) {
			this.command = command;
		}
		
		@Override
		public String consume(Matcher matcher, String source, String user) throws InputException {
			return command.execute(Dicebot.this, matcher, source, user);
		}
	}
	
	/** A command to display help to a user. */
	private class HelpCommand implements Command {
		@Override
		public String execute(Dicebot dicebot, Matcher matcher, String source, String user) {
			if (matcher.groupCount() >= 1) {
				String secondary = (matcher.group(1) != null ? matcher.group(1).trim() : "");
				if (rollers.containsKey(secondary)) {
					List<String> help = rollers.get(secondary).getDescription();
					sendRollerHelp(user, secondary, help);
				} else if (commands.containsKey(secondary)) {
					String help = commands.get(secondary).getDescription();
					sendMessage(user, secondary + " : " + help);
				} else {
					sendMainHelp(user);
				}
			}
			
			return null;
		}
		
		/** Sends the main help to the user. */
		private void sendMainHelp(String user) {
			List<String> commandList = buildHelpList("Here is a list of the commands I can perform: ", commands);
			for (String string : commandList) {
				sendMessage(user, string);
			}
			sendMessage(user, "For more details on the dice systems, try !help command, replacing \"command\" with the actual name of the command. Prefix all commands with !.");
			List<String> rollerList = buildHelpList("Here is a list of the dice systems I can handle: ", rollers);
			for (String string : rollerList) {
				sendMessage(user, string);
			}
			sendMessage(user, "For more details on the dice systems, try !help systemName, replacing \"systemName\" with the actual name of the system.");
		}
		
		/** 
		 * Builds a list of objects into a comma-delimited string, making sure there's no cut off lines.
		 */
		private List<String> buildHelpList(String base, Map<String, ?> sourceMap) {
			List<String> commandList = new ArrayList<String>();
			String next = base;
			for (String command : sourceMap.keySet()) {
				if (next.length() + command.length() > getMaxLineLength()) {
					next = next.substring(0, next.length() - 2);
					commandList.add(next);
					next = "";
				}
				next += command + ", ";
			}
			next = next.substring(0, next.length() - 2);
			commandList.add(next);
			return commandList;
		}
		
		/** Sends a specific roller's help to the user. */
		private void sendRollerHelp(String user, String name, List<String> help) {
			sendMessage(user, "Help for the " + name + " roller:");
			for (String line : help) {
				sendMessage(user, line);
			}
		}
		
		@Override
		public String getDescription() {
			return "Displays this help.";
		}
		
		@Override
		public String getRegexp() {
			Joiner joiner = Joiner.on("| ");
			String rollerNames = joiner.join(rollers.keySet());
			String commandNames = joiner.join(commands.keySet());
			return "help( " + commandNames + "| " + rollerNames + ")?";
		}
	}
}
