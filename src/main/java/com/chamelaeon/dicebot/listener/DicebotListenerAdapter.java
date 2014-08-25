/**
 * 
 */
package com.chamelaeon.dicebot.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.chamelaeon.dicebot.Dicebot;
import com.chamelaeon.dicebot.InputException;
import com.chamelaeon.dicebot.commands.Command;
import com.chamelaeon.dicebot.commands.HelpDetails;

/**
 * An extension to the PircBotX ListenerAdapter that processes the event for a match to a regexp pattern.
 * @author Chamelaeon
 */
public abstract class DicebotListenerAdapter extends ListenerAdapter<Dicebot> implements Command {
    /** The help details for this listener. */
	private final HelpDetails helpDetails;
	/** The command pattern for this listener. */
	private final Pattern commandPattern;

	/**
	 * Constructor.
	 * @param commandPattern The command pattern for this listener.
	 * @param helpDetails The help details for this listener.
	 */
	public DicebotListenerAdapter(String commandPattern, HelpDetails helpDetails) {
		this.helpDetails = helpDetails;
		this.commandPattern = Pattern.compile(commandPattern);
	}

	@Override
	public void onMessage(MessageEvent<Dicebot> event) throws Exception {
		parseMessage(new DicebotGenericEvent<>(event.getChannel(), event));
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<Dicebot> event) throws Exception {
		parseMessage(new DicebotGenericEvent<>(event.getUser(), event));
	}

	@Override
    public HelpDetails getHelpDetails() {
        return helpDetails;
    }
	
	/**
	 * Performs the actual message parse, and if the pattern of the event text matches, it dispatches it via onSuccess.
	 * @param event The event to dispatch.
	 */
	public void parseMessage(DicebotGenericEvent<Dicebot> event) {
		// Check for any match.
		Matcher matcher = commandPattern.matcher(event.getMessage());
		if (matcher.find()) {
			
			List<String> groups = new ArrayList<>();
			// Process all the groups.
			for (int i = 0; i <= matcher.groupCount(); i++) {
				groups.add(StringUtils.trim(matcher.group(i)));
			}
			
			try {
                onSuccess(event, groups);
            } catch (InputException ie) {
                event.respond(ie.getMessage());
            }
		}
	}
	
	/**
	 * Called if the message event successfully matches the given command pattern.
	 * @param event The event which matched.
	 * @param groups All regexp groups matched from the pattern.
	 * @throws InputException if there is a problem with the input.
	 */
	public abstract void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) throws InputException;
}
