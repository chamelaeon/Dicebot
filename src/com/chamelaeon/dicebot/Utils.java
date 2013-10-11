package com.chamelaeon.dicebot;

import java.util.List;

import com.chamelaeon.dicebot.dice.DieResult;
import com.chamelaeon.dicebot.personality.Personality;

/**
 * Utility methods common across classes.
 * @author Chamelaeon
 */
public class Utils {
	/**
	 * Safely parses a string into an short.
	 * @param shortString The string to parse.
	 * @param personality The personality to handle the exception.
	 * @return the parsed short.
	 * @throws InputException if the string could not be parsed.
	 */
	public static short parseShort(String shortString, Personality personality) throws InputException {
		try {
			return Short.parseShort(shortString);
		} catch (NumberFormatException nfe) {
			throw personality.getException("ParseBadShort", shortString);
		}
	}

	/**
	 * Parses the dice count portion of the roll and replaces NULL with 1.
	 * @param diceCountString The dice count string to parse.
	 * @param personality The personality to handle the exception.
	 * @return the number of dice to roll.
	 * @throws InputException if the dice rolls could not be parsed.
	 */
	public static short parseDiceCount(String diceCountString, Personality personality) throws InputException {
		if (null != diceCountString && !diceCountString.isEmpty()) {
			return parseShort(diceCountString, personality);
		} else {
			return 1;
		}
	}
	
	/**
	 * Sums the first <code>count</code> values of the given list.
	 *  
	 * @param list The list to sum.
	 * @param count The first X items to sum.
	 * @return the sum.
	 */
	public static long sumFirst(List<DieResult> list, int count) {
		long total = 0;
		for (int i = 0; i < count; i++) {
			total += list.get(i).getResult();
		}
		return total;
	}
}
