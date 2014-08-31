package com.chamelaeon.dicebot.personality;

import java.util.Arrays;
import java.util.Properties;


public class PropertiesPersonality extends AbstractPersonality {

	/**
	 * Constructs a {@link PropertiesPersonality} from the given Properties file.
	 * @param props The properties to load from.
	 */
	public PropertiesPersonality(Properties props) {
		configurableTexts.put("LessThanOneGroup", props.getProperty("LessThanOneGroup"));
		configurableTexts.put("Roll0Dice", props.getProperty("Roll0Dice"));
		configurableTexts.put("Roll0Sides", props.getProperty("Roll0Sides"));
		configurableTexts.put("OneSidedDice", props.getProperty("OneSidedDice"));
		configurableTexts.put("KeepingLessThan1", props.getProperty("KeepingLessThan1"));
		configurableTexts.put("RollLessThanKeep", props.getProperty("RollLessThanKeep"));
		configurableTexts.put("BadCommand", props.getProperty("BadCommand"));
		configurableTexts.put("CannotSatisfyRerollSingleDie", props.getProperty("CannotSatisfyRerollSingleDie"));
		configurableTexts.put("CannotSatisfyRerollMultipleDice", props.getProperty("CannotSatisfyRerollMultipleDice"));
		configurableTexts.put("InfiniteExplosion", props.getProperty("InfiniteExplosion"));
		configurableTexts.put("BrokenRegexp", props.getProperty("BrokenRegexp"));
		configurableTexts.put("ParseBadShort", props.getProperty("ParseBadShort"));
		configurableTexts.put("ReflectionError", props.getProperty("ReflectionError"));
		configurableTexts.put("CannotSatisfySuccesses", props.getProperty("CannotSatisfySuccesses"));
		configurableTexts.put("Roll0Dice", props.getProperty("Roll0Dice"));
		configurableTexts.put("Cheat", props.getProperty("Cheat"));
		
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
