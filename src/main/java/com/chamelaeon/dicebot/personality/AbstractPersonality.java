package com.chamelaeon.dicebot.personality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.random.BasicRandom;
import com.chamelaeon.dicebot.random.Random;

public abstract class AbstractPersonality implements Personality {

	/** The bot text strings, mapped by key. */
	final Map<String, String> outputTexts;

	/** Whether or not to show messages for each roll result type, mapped by the roll result type. */
	final Map<String, AtomicBoolean> rollResultFlags;

	/** The available message lists for each roll result type, mapped by the roll result type. */
	final Map<String, List<String>> rollResultMessageLists;

	/** The random for selecting random values. */
	final Random random;

	/** Constructor. */
	AbstractPersonality() {
		outputTexts = new HashMap<>();
		rollResultMessageLists = new HashMap<>();
		rollResultFlags = new HashMap<>();
		random = new BasicRandom();
	}

	@Override
    public InputException getException(String key, TokenSubstitution... params) {
		return new InputException(performTokenSubstitution(outputTexts.get(key), params));
	}

	@Override
    public String getMessage(String key, TokenSubstitution... params) {
        return performTokenSubstitution(outputTexts.get(key), params);
    }

	@Override
    public String getRollResult(String key, TokenSubstitution... params) {
		return performTokenSubstitution(outputTexts.get(key), params);
	}

	@Override
	public boolean shouldShowMessagesForRollResultType(String rollResultType) {
		if (rollResultFlags.containsKey(rollResultType)) {
			return rollResultFlags.get(rollResultType).get();
		} else {
			return false;
		}
	}

	@Override
	public String chooseRollResultTypeCommentLine(String rollResultType) {
		if (rollResultMessageLists.containsKey(rollResultType)) {
			List<String> rollResultCommentLines = rollResultMessageLists.get(rollResultType);
			return rollResultCommentLines.get(random.getRoll(rollResultCommentLines.size()) - 1);
		} else {
			return "";
		}
	}

    @Override
    public short parseShort(String shortString) throws InputException {
        try {
            return Short.parseShort(shortString);
        } catch (NumberFormatException nfe) {
            throw getException("ParseBadShort", new TokenSubstitution("%BADSHORT%", shortString));
        }
    }

    @Override
    public short parseDiceCount(String diceCountString) throws InputException {
        if (null != diceCountString && !diceCountString.isEmpty()) {
            return parseShort(diceCountString);
        } else {
            return 1;
        }
    }

    /**
     * Performs the actual token substitution. All tokens are substituted - ones whose tokens do not exist in the
     * format string are ignored. Tokens that are not substituted are left in place.
     *
     * If performance suffers, we can unspool the TokenSubstitution objects and use StringUtils.replace(string, String[], String[])
     * which intentionally avoids object creation.
     *
     * @param formatString The string to apply substitutions to.
     * @param substitutions The substitutions to make.
     * @return the substituted string.
     */
    protected String performTokenSubstitution(String formatString, TokenSubstitution... substitutions) {
        String retStr = formatString;
        for (TokenSubstitution substitution : substitutions) {
            retStr = StringUtils.replace(retStr, substitution.getToken(), substitution.getSubstitution());
        }

        return retStr;
    }
}
