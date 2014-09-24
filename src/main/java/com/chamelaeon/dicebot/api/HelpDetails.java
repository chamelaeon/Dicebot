package com.chamelaeon.dicebot.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Details about the command, for use in the help system.
 * @author Chamelaeon
 */
public class HelpDetails {
    /** The description of the command. */
	private final String description;
	/** The "name" of the command, or what the user has to type to activate it. */
	private final String name;
	/** The type of the command, e.g. "command" or "roller", typically. */
    private final String type;
	/** Any examples of the command. */
	private final List<String> examples;

	/**
     * Constructor.
     * @param name The name of the command.
     * @param description The description of the command.
     * @param type The type of command.
     */
    public HelpDetails(String name, String description, String type) {
        this(name, description, type, new ArrayList<String>());
    }
    
    /**
     * Constructor.
     * @param name The name of the command.
     * @param description The description of the command.
     */
    public HelpDetails(String name, String description) {
        this(name, description, new ArrayList<String>());
    }
	
    /**
     * Constructor.
     * @param name The name of the command.
     * @param description The description of the command.
     * @param type The type of command.
     * @param examples Some examples of the command being used. An empty list will cause no 
     *                 examples section to print in the help for this command.
     */
    public HelpDetails(String name, String description, List<String> examples) {
        this(name, description, "command", examples);
    }
    
	/**
	 * Constructor.
	 * @param name The name of the command.
	 * @param description The description of the command.
	 * @param type The type of command.
	 * @param examples Some examples of the command being used. An empty list will cause no 
	 *                 examples section to print in the help for this command.
	 */
	public HelpDetails(String name, String description, String type, List<String> examples) {
		this.description = description;
		this.name = name;
		this.type = type;
		this.examples = examples;
	}

	/**
	 * Returns the description of the command.
	 * @return the description.
	 */
	public String getDescription() {
		return description;
	};
	
	/**
	 * Returns the name of the command.
	 * @return the name.
	 */
	public String getCommandName() {
		return name;
	};
	
	/**
     * Returns the type of the command.
     * @return the type.
     */
    public String getType() {
        return type;
    };
	
	/**
	 * Returns the examples of the command.
	 * @return the examples.
	 */
	public List<String> getExamples() {
		return examples;
	}
}
