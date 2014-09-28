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
	public PropertiesPersonality(Properties props) {
	    for (Entry<Object, Object> entry : props.entrySet()) {
	        
	        if (!configKeys.contains(entry.getKey())) {
	            outputTexts.put(entry.getKey().toString(), entry.getValue().toString());
	        }
        }
	    
		useCritSuccesses.set(Boolean.parseBoolean(props.getProperty("UseCriticalSuccessMessages").trim()));
		useCritFailures.set(Boolean.parseBoolean(props.getProperty("UseCriticalFailureMessages").trim()));
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
	
	@Override
	public String getStatus() {
		return "Copacetic, one supposes.";
	}
}
