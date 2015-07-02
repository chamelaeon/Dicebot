package com.chamelaeon.dicebot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;

/** 
 * A command to display help to a user.
 * @author Chamelaeon
 */
public class HelpCommand extends DicebotListenerAdapter {
	/** The map of commands to their help details. */
	private Map<String, HelpDetails> commandDetailsMap;
	/** The map of rollers to their help details. */
	private Map<String, HelpDetails> rollerDetailsMap;

	/**
	 * Constructor.
	 * @param commandHelpDetails The map of commands to their help details.
	 * @param rollerHelpDetails The map of rollers to their help details.
	 */
	public HelpCommand(List<HelpDetails> commandHelpDetails, List<HelpDetails> rollerHelpDetails) {
		super(buildRegexp(commandHelpDetails, rollerHelpDetails), new HelpDetails("help", "Displays this help."));
		commandDetailsMap  = new HashMap<>();
		rollerDetailsMap  = new HashMap<>();
		
		for (HelpDetails details : commandHelpDetails) {
			commandDetailsMap.put(details.getCommandName(), details);
		}
		commandDetailsMap.put(this.getHelpDetails().getCommandName(), this.getHelpDetails());
		
		for (HelpDetails details : rollerHelpDetails) {
			rollerDetailsMap.put(details.getCommandName(), details);
		}
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
		if (groups.size() > 1 && null != groups.get(1)) {
		    String helpPhrase = groups.get(1);
		    if (commandDetailsMap.containsKey(helpPhrase)
		            || rollerDetailsMap.containsKey(helpPhrase)) {
		        
		        HelpDetails details = MoreObjects.firstNonNull(
		                commandDetailsMap.get(helpPhrase),
		                rollerDetailsMap.get(helpPhrase));
		        
		        sendHelp(event, details);
		    }
		} else {
			sendMainHelp(event);
		}
	}

	/**
	 * Sends the main help to the user.
	 * @param event The event that triggered this output.
	 */
	private void sendMainHelp(DicebotGenericEvent<Dicebot> event) {
		List<String> commandList = buildHelpList("Here is a list of the commands I can perform: ", new ArrayList<HelpDetails>(commandDetailsMap.values()));
		for (String string : commandList) {
			event.respond(string);
		}
		event.respond("For more details on the commands, try !help [command], replacing \"[command]\" with the actual name of the command. Prefix all commands with !.");
		List<String> rollerList = buildHelpList("Here is a list of the dice systems I can handle: ", new ArrayList<HelpDetails>(rollerDetailsMap.values()));
		for (String string : rollerList) {
			event.respond(string);
		}
		event.respond("For more details on the dice systems, try !help [systemName], replacing \"[systemName]\" with the actual name of the system.");
	}
	
	/** Sends a specific help to the user. */
	private void sendHelp(DicebotGenericEvent<Dicebot> event, HelpDetails details) {
		event.respond("Help for the " + details.getCommandName() + " " + details.getType() + ":");
		event.respond(details.getDescription());
		
		if (details.getExamples().size() > 0) {
		    event.respond("Examples:");
		    for (String example : details.getExamples()) {
                event.respond(example);
            }
		}
	}
	
	/** 
	 * Builds a list of objects into a comma-delimited string, making sure there's no cut off lines.
	 * @param base The initial line.
	 * @param details All the HelpDetails that need to be output.
	 */
	private List<String> buildHelpList(String base, List<HelpDetails> details) {
		List<String> commandList = new ArrayList<String>();
		Collections.sort(details);
		String next = base;
		for (HelpDetails detail : details) {
			if (next.length() + detail.getCommandName().length() > 600) {
				next = next.substring(0, next.length() - 2);
				commandList.add(next);
				next = "";
			}
			next += detail.getCommandName() + ", ";
		}
		next = next.substring(0, next.length() - 2);
		commandList.add(next);
		return commandList;
	}
	
	/**
	 * Builds the regexp for the help command.
	 * @param commandHelpDetails The map of commands to their help details.
	 * @param rollerHelpDetails The map of rollers to their help details.
	 * @return the regexp for the help command.
	 */
	public static String buildRegexp(List<HelpDetails> commandHelpDetails, List<HelpDetails> rollerHelpDetails) {
		List<HelpDetails> allDetails = new ArrayList<>(commandHelpDetails);
		allDetails.addAll(rollerHelpDetails);
		
		Joiner joiner = Joiner.on("| ");
		List<String> commands = new ArrayList<>();
		for (HelpDetails details : allDetails) {
			commands.add(details.getCommandName());
		}
		commands.add("help");

		String commandNames = joiner.join(commands);
		return "!help( " + commandNames + ")?";
	}
}