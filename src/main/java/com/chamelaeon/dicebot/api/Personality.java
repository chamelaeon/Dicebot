package com.chamelaeon.dicebot.api;

public interface Personality {
    /**
     * Gets the user-facing {@link InputException} represented by the key.
     * @param key The exception's individual key.
     * @param substitutions The token substitutions to make in the exception message.
     * @return the exception to throw up to where a user can see it.
     */
    InputException getException(String key, TokenSubstitution... substitutions);

    /**
     * Gets a simple configurable message.
     * @param key The key of the message to get.
     * @param substitutions The token substitutions to make in the message.
     * @return the message.
     */
    String getMessage(String key, TokenSubstitution... substitutions);

    /**
     * Gets the roll result text for the given key.
     * @param key The roll result's key.
     * @param substitutions The token substitutions to make in the roll result.
     * @return the filled-out roll results.
     */
    String getRollResult(String key, TokenSubstitution... substitutions);

    /**
     * Returns whether or not this personality allows messages for the given type of roll result.
     * This allows the personality to show or hide for each given roll result. For instance, it could hide critical
     * failures but show critical successes, or hide all but miss messages for the PbtA roller, or hide all the PbtA
     * roll result messages entirely.
     * @param rollResultType The type of roll result to check for allowing.
     * @return true if messages for that result type are allowed, false otherwise.
     */
    boolean shouldShowMessagesForRollResultType(String rollResultType);

    /**
     * Returns a random personality comment line from the available selection for that roll result type.
     * @param rollResultType The type of roll result to get a comment line for.
     * @return the selected comment line for that roll result type.
     */
    String chooseRollResultTypeCommentLine(String rollResultType);

    /**
     * Safely parses a string into an short.
     * @param shortString The string to parse.
     * @return the parsed short.
     * @throws InputException if the string could not be parsed.
     */
    short parseShort(String shortString) throws InputException;

    /**
     * Parses the dice count portion of the roll and replaces NULL with 1.
     * @param diceCountString The dice count string to parse.
     * @return the number of dice to roll.
     * @throws InputException if the dice rolls could not be parsed.
     */
    short parseDiceCount(String diceCountString)
            throws InputException;

}
