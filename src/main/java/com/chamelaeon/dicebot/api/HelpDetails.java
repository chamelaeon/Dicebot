package com.chamelaeon.dicebot.api;

/**
 * Details about the command, for use in the help system.
 * @author Chamelaeon
 */
public class HelpDetails {
    /** The description of the command. */
	private final String description;
	/** The "name" of the command, or what the user has to type to activate it. */
	private final String name;
	/** Any examples of the command. */
	private final String examples;

	/**
	 * Constructor.
	 * @param name The name of the command.
	 * @param description The description of the command.
	 */
	public HelpDetails(String name, String description) {
		this.description = description;
		this.name = name;
		this.examples = "";
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
	 * Returns the examples of the command.
	 * @return the examples.
	 */
	public String getExamples() {
		return examples;
	}
}
