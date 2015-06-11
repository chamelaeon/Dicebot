package com.chamelaeon.dicebot.commands;

import java.util.List;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;

/**
 * Command that prints the version.
 * @author Chamelaeon
 */
public class VersionCommand extends DicebotListenerAdapter {

    /** The version the dicebot is running. */
    private final String version;
    
    /**
     * Constructor.
     * @param version The version this dicebot is running, to be printed.
     */
    public VersionCommand(String version) {
        super("!version", new HelpDetails("version", "Prints the version this bot is running."));
        this.version = version;
    }

    @Override
    public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
        event.respond("This dicebot is running version " + version + ".");
    }
}
