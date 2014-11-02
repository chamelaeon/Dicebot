package com.chamelaeon.dicebot.rollers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.DieResult;
import com.chamelaeon.dicebot.dice.FudgeDie;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.dice.Modifier;
import com.chamelaeon.dicebot.dice.Roll;

/** A roller for Fudge behavior, e.g. "4dF". */
public class FudgeRoller extends Roller {
	
	/** Regex piece for the basic roll. */
    private static final String BASIC_ROLL_REGEX = "(\\d+)d[fF]";
    /** The complete regex for the roller. */
    private static final String TOTAL_REGEX = "^" + GROUP_REGEX + BASIC_ROLL_REGEX + MODIFIER_REGEX + ANNOTATION_REGEX + "$";
	
    /** Descriptors for most potential Fudge roll results. */
	private static final Map<Long, String> descriptors = new HashMap<Long, String>() {
		private static final long serialVersionUID = -7890866934527969200L;
	{
		long idx = -4;
		put(idx++, "Unfathomably Bad");
		put(idx++, "Miserable");
		put(idx++, "Terrible");
		put(idx++, "Poor");
		put(idx++, "Mediocre");
		put(idx++, "Average");
		put(idx++, "Fair");
		put(idx++, "Good");
		put(idx++, "Great");
		put(idx++, "Superb");
		put(idx++, "Fantastic");
		put(idx++, "Epic");
		put(idx++, "Legendary");
		put(idx++, "Phat!");
		put(idx++, "Modular");
		put(idx++, "Schway");
		put(idx++, "Truly Outrageous");
		put(idx++, "Off The Chain!");
		put(idx++, "Ostentatious");
		put(idx++, "PENTAKILL");
	}};
	
	/**
	 * Constructor.
	 * @param personality The object containing the dicebot personality.
	 */
	public FudgeRoller(Personality personality) {
		super(TOTAL_REGEX, "Fudge",
				"A dice roller for the FUDGE dice style, which rolls X number of d6s with faces of ['-', '-', ' ', ' ', '+', '+'] and returns the additive result (ex. 4dF).",
				Arrays.asList("Basic roll: 4dF", "Roll with modifier: 4dF+3"),
				personality);
	}

	@Override
	protected String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
		short groupCount = parseGroups(parts[1]);
		short rolled = getPersonality().parseDiceCount(parts[2]);
		Modifier modifier = Modifier.createModifier(parts[3], getPersonality());
		String annotation = getAnnotationString(parts[4]);
		
		if (rolled < 1) {
			throw getPersonality().getException("Roll0Dice");
		}
		
		Roll roll = new Roll(rolled, rolled, new FudgeDie(), modifier, null, null, getPersonality());
		List<GroupResult> groups = roll.performRoll(groupCount, random, statistics);
		
		String textKey;
		String natural;
		String modified;
		String descriptor = "";
		List<String> convDice = null;
		if (groups.size() > 1) {
		    textKey = "FudgeMoreGroups";
            natural = buildNaturalList(groups);
            modified = buildModifiedList(groups);
        } else {
            textKey = "Fudge1Group";
            GroupResult group = groups.get(0);
            natural = Long.toString(group.getNatural());
            modified = Long.toString(group.getModified());
            convDice = convertToFudgeDie(group.getDice());
            
            descriptor = descriptors.get(group.getModified());
            if (null == descriptor) {
                if (group.getModified() > 0) {
                    descriptor = "Off The Scale!";
                } else {
                    descriptor = "Did You Have Breakfast Today?";
                }
            }
        }
		
		return getPersonality().getRollResult(textKey, 
		        new TokenSubstitution("%GROUPCOUNT%", groupCount), new TokenSubstitution("%DICECOUNT%", rolled),
                new TokenSubstitution("%MODIFIER%", modifier), new TokenSubstitution("%USER%", user), 
                new TokenSubstitution("%FUDGEVALUE%", convDice), new TokenSubstitution("%NATURALVALUE%", natural), 
                new TokenSubstitution("%MODIFIEDVALUE%", modified), new TokenSubstitution("%DESCRIPTOR%", descriptor),
		        new TokenSubstitution("%ANNOTATION%", annotation));
	}

	protected static String getRegexp() {
	    return TOTAL_REGEX;
	}
	
	/**
	 * Converts normal integer dice into fudge dice.
	 * @param dice The dice to convert.
	 * @return the corresponding fudge dice.
	 */
	private List<String> convertToFudgeDie(List<DieResult> dice) {
		List<String> retList = new ArrayList<String>();
		for (DieResult die : dice) {
			if (die.getResult() == 0) {
				retList.add("o");
			} else if (die.getResult() == -1) {
				retList.add("-");
			} else if (die.getResult() == 1) {
				retList.add("+");
			}
		}
		return retList;
	}
}