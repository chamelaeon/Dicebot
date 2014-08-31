package com.chamelaeon.dicebot.personality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.random.BasicRandom;
import com.chamelaeon.dicebot.random.Random;


public abstract class AbstractPersonality implements Personality {

	/** The exception texts, mapped by key. */
	final Map<String, String> configurableTexts;
	
	/** The roll outputs, mapped by key. */
	final Map<String, String> rollOutputs;
	
	/** The available critical failure message list. */
	final List<String> criticalFailures;
	
	/** The available critical failure message list. */
	final List<String> criticalSuccesses;
	
	/** Whether or not to use critical success messages. */
	final AtomicBoolean useCritSuccesses;
	
	/** Whether or not to use critical failure messages. */
	final AtomicBoolean useCritFailures;
	
	final Random random;
	
	/** Constructor. */
	AbstractPersonality() {
		configurableTexts = new HashMap<String, String>();
		rollOutputs = new HashMap<String, String>();
		criticalFailures = new ArrayList<String>();
		criticalSuccesses = new ArrayList<String>();
		useCritSuccesses = new AtomicBoolean(true);
		useCritFailures = new AtomicBoolean(true);
		random = new BasicRandom();
	}
	
	@Override
    public InputException getException(String key, Object... params) {
		return new InputException(String.format(configurableTexts.get(key), params));
	}

	@Override
    public String getMessage(String key) {
		return configurableTexts.get(key);
	}
	
	@Override
    public String getRollResult(String key, Object... params) {
		return String.format(rollOutputs.get(key), params);
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
    public abstract String getStatus();

    @Override
    public short parseShort(String shortString) throws InputException {
        try {
            return Short.parseShort(shortString);
        } catch (NumberFormatException nfe) {
            throw getException("ParseBadShort", shortString);
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
}