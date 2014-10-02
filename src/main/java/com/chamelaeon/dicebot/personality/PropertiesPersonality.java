package com.chamelaeon.dicebot.personality;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Class which uses a properties file to derive all necessary personality data.
 * @author Chamelaeon
 */
public class PropertiesPersonality extends AbstractPersonality {

    /**
	 * Constructs a {@link PropertiesPersonality} from the given Properties file.
	 * @param props The properties to load from.
	 */
	public PropertiesPersonality(Properties props, boolean useCritSuccesses, boolean useCritFailures) {
	    for (Entry<Object, Object> entry : props.entrySet()) {
            outputTexts.put(entry.getKey().toString(), entry.getValue().toString());
        }
	    
		this.useCritSuccesses.set(useCritSuccesses);
		this.useCritFailures.set(useCritFailures);
		if (useCritSuccesses()) {
			String critSucc = props.getProperty("CriticalSuccesses");
			String[] critSuccSplit = critSucc.split("#");
			criticalSuccesses.addAll(Arrays.asList(critSuccSplit));
		}
		if (useCritFailures()) {
			String critFail = props.getProperty("CriticalFailures");
			String[] critFailSplit = critFail.split("#");
			criticalFailures.addAll(Arrays.asList(critFailSplit));
		}
	}
}
