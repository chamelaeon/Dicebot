package com.chamelaeon.dicebot.personality;

import java.util.Arrays;
import java.util.Properties;

public class PropertiesPersonality extends Personality {

	/**
	 * Constructs a {@link PropertiesPersonality} from the given Properties file.
	 * @param props The properties to load from.
	 * @param cardPath The path to the card database.
	 */
	public PropertiesPersonality(Properties props, String cardPath) {
		super(cardPath);
		exceptionTexts.put("LessThanOneGroup", props.getProperty("LessThanOneGroup"));
		exceptionTexts.put("Roll0Dice", props.getProperty("Roll0Dice"));
		exceptionTexts.put("Roll0Sides", props.getProperty("Roll0Sides"));
		exceptionTexts.put("OneSidedDice", props.getProperty("OneSidedDice"));
		exceptionTexts.put("KeepingLessThan1", props.getProperty("KeepingLessThan1"));
		exceptionTexts.put("RollLessThanKeep", props.getProperty("RollLessThanKeep"));
		exceptionTexts.put("BadCommand", props.getProperty("BadCommand"));
		exceptionTexts.put("CannotSatisfyRerollSingleDie", props.getProperty("CannotSatisfyRerollSingleDie"));
		exceptionTexts.put("CannotSatisfyRerollMultipleDice", props.getProperty("CannotSatisfyRerollMultipleDice"));
		exceptionTexts.put("InfiniteExplosion", props.getProperty("InfiniteExplosion"));
		exceptionTexts.put("BrokenRegexp", props.getProperty("BrokenRegexp"));
		exceptionTexts.put("ParseBadShort", props.getProperty("ParseBadShort"));
		exceptionTexts.put("ReflectionError", props.getProperty("ReflectionError"));
		exceptionTexts.put("CannotSatisfySuccesses", props.getProperty("CannotSatisfySuccesses"));
		exceptionTexts.put("Roll0Dice", props.getProperty("Roll0Dice"));
		
		rollOutputs.put("Standard1Group", props.getProperty("Standard1Group"));
		rollOutputs.put("Standard1GroupCrit", props.getProperty("Standard1GroupCrit"));
		rollOutputs.put("StandardMoreGroups", props.getProperty("StandardMoreGroups"));
		rollOutputs.put("L5ROneGroup", props.getProperty("L5ROneGroup"));
		rollOutputs.put("L5RMoreGroups", props.getProperty("L5RMoreGroups"));
		rollOutputs.put("WhiteWolfSuccess", props.getProperty("WhiteWolfSuccess"));
		rollOutputs.put("WhiteWolfFailure", props.getProperty("WhiteWolfFailure"));
		rollOutputs.put("WhiteWolfBotch", props.getProperty("WhiteWolfBotch"));
		rollOutputs.put("Fudge1Group", props.getProperty("Fudge1Group"));
		rollOutputs.put("FudgeMoreGroups", props.getProperty("FudgeMoreGroups"));
		
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
