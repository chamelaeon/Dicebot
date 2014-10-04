package com.chamelaeon.dicebot.personality;

import java.util.ArrayList;
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
	
	/** The available critical failure message list. */
	final List<String> criticalFailures;
	
	/** The available critical failure message list. */
	final List<String> criticalSuccesses;
	
	/** Whether or not to use critical success messages. */
	final AtomicBoolean useCritSuccesses;
	
	/** Whether or not to use critical failure messages. */
	final AtomicBoolean useCritFailures;
	
	/** The random for selecting random values. */
	final Random random;
	
	/** Constructor. */
	AbstractPersonality() {
		outputTexts = new HashMap<String, String>();
		criticalFailures = new ArrayList<String>();
		criticalSuccesses = new ArrayList<String>();
		useCritSuccesses = new AtomicBoolean(true);
		useCritFailures = new AtomicBoolean(true);
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
    public boolean useCritSuccesses() {
		return useCritSuccesses.get();
	}
	
	@Override
    public boolean useCritFailures() {
		return useCritFailures.get();
	}

	@Override
    public String chooseCriticalFailureLine() {
		return criticalFailures.get(random.getRoll(criticalFailures.size()) - 1);
	}
	
	@Override
    public String chooseCriticalSuccessLine() {
		return criticalSuccesses.get(random.getRoll(criticalSuccesses.size()) - 1);
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