package com.chamelaeon.dicebot.personality;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class which uses a properties file to derive all necessary personality data.
 * @author Chamelaeon
 */
public class PropertiesPersonality extends AbstractPersonality {

    /**
	 * Constructs a {@link PropertiesPersonality} from the given Properties file.
	 * @param props The properties to load from.
	 */
	public PropertiesPersonality(Properties props) {
	    for (Entry<Object, Object> entry : props.entrySet()) {
            outputTexts.put(entry.getKey().toString(), entry.getValue().toString());
        }

		String useCritSuccesses = props.getProperty("UseCriticalSuccessMessages");
		if (Boolean.parseBoolean(useCritSuccesses)) {
			String critSucc = props.getProperty("CriticalSuccesses");
			rollResultFlags.put("criticalSuccess", new AtomicBoolean(true));
			String[] critSuccSplit = critSucc.split("#");
			rollResultMessageLists.put("criticalSuccess", Arrays.asList(critSuccSplit));
		}

		String useCritFailures = props.getProperty("UseCriticalSuccessMessages");
		if (Boolean.parseBoolean(useCritFailures)) {
			String critFail = props.getProperty("CriticalFailures");
			rollResultFlags.put("criticalFailure", new AtomicBoolean(true));
			String[] critFailSplit = critFail.split("#");
			rollResultMessageLists.put("criticalFailure", Arrays.asList(critFailSplit));
		}
	}
}
