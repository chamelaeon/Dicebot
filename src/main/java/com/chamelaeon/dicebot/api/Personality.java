package com.chamelaeon.dicebot.api;


public interface Personality {

    /**
     * Gets the user-facing {@link InputException} represented by the key.  
     * @param key The exception's individual key.
     * @param params The parameters to fill out for the exception.
     * @return the exception to throw up to where a user can see it.
     */
    public abstract InputException getException(String key, Object... params);

    /**
     * Gets a simple configurable message.
     * @param key The key of the message to get.
     * @return the message.
     */
    public abstract String getMessage(String key);
    
    /**
     * Gets a simple configurable message.
     * @param key The key of the message to get.
     * @param params The parameters to fill out for the message.
     * @return the message.
     */
    public abstract String getMessage(String key, Object... params);

    /**
     * Gets the roll result text for the given key.
     * @param key The roll result's key.
     * @param params The parameters to fill out for the roll result.
     * @return the filled-out roll results.
     */
    public abstract String getRollResult(String key, Object... params);

    /**
     * Returns whether or not this personality allows critical success messages or not.
     * @return true if critical success messages are allowed, false otherwise.
     */
    public abstract boolean useCritSuccesses();

    /**
     * Returns whether or not this personality allows critical failure messages or not.
     * @return true if critical failure messages are allowed, false otherwise.
     */
    public abstract boolean useCritFailures();

    /**
     * Picks a random critical failure line from the available selection.
     * @return the selected critical failure line.
     */
    public abstract String chooseCriticalFailureLine();

    /**
     * Picks a random critical success line from the available selection.
     * @return the selected critical success line.
     */
    public abstract String chooseCriticalSuccessLine();

    /**
     * Safely parses a string into an short.
     * @param shortString The string to parse.
     * @return the parsed short.
     * @throws InputException if the string could not be parsed.
     */
    public abstract short parseShort(String shortString) throws InputException;

    /**
     * Parses the dice count portion of the roll and replaces NULL with 1.
     * @param diceCountString The dice count string to parse.
     * @return the number of dice to roll.
     * @throws InputException if the dice rolls could not be parsed.
     */
    public abstract short parseDiceCount(String diceCountString)
            throws InputException;

}