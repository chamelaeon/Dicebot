package com.chamelaeon.dicebot.rollers;

import java.util.Arrays;
import java.util.List;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.dice.Modifier;
import com.chamelaeon.dicebot.dice.Roll;
import com.chamelaeon.dicebot.dice.SimpleDie;
import com.chamelaeon.dicebot.dice.behavior.Behavior;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.dice.behavior.Behavior.BehaviorsPair;
import com.chamelaeon.dicebot.random.Random;

/** 
 * A roller for handling standard die behavior, like "2d6" or "1d20". 
 * @author Chamelaeon
 */
public class StandardRoller extends Roller {
	/** Regex piece for the basic roll. */
    private static final String BASIC_ROLL_REGEX = "(\\d*)d(\\d+)";
    /** Regex piece for behavior matching. */
    private static final String BEHAVIORS_REGEX = "(b[1-9]|v(?:[1-9][0-9]?)?)?";
    /** The complete regex for the roller. */
    private static final String TOTAL_REGEX = "^" + GROUP_REGEX + BASIC_ROLL_REGEX + BEHAVIORS_REGEX + MODIFIER_REGEX + BEHAVIORS_REGEX 
    		+ ANNOTATION_REGEX + "$";
    
	/**
	 * Constructor.
	 * @param personality The object containing the dicebot personality.
	 */
	public StandardRoller(Personality personality) {
	    super(TOTAL_REGEX, "Standard", getDesc(),
                Arrays.asList("Normal rolls: 2d6, d20", "Roll with modifier: d20+3", "Roll with Brutal 2: 2d6b2",
                        "Roll with basic vorpal: d10v", "Roll with expanded vorpal (5-10 range): d10v6",
                        "Roll with brutal 2 and modifier: 2d6b2+5", "Roll with vorpal and modifier: 2d6v+3"),
                personality);
	}
	
	/**
     * Protected Constructor for testing.
     * @param personality The object containing the dicebot personality.
     * @param random The random for testing.
     */
    StandardRoller(Personality personality, Random random) {
		super(TOTAL_REGEX, "Standard", getDesc(),
		        Arrays.asList("Normal rolls: 2d6, d20", "Roll with modifier: d20+3", "Roll with Brutal 2: 2d6b2",
		                "Roll with basic vorpal: d10v", "Roll with expanded vorpal (5-10 range): d10v6",
		                "Roll with brutal 2 and modifier: 2d6b2+5", "Roll with vorpal and modifier: 2d6v+3"),
				personality, random);
	}
	
	@Override
	public String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
		short groupCount = parseGroups(parts[1]);
		short diceCount = getPersonality().parseDiceCount(parts[2]);
		short diceType = getPersonality().parseShort(parts[3]);
		Modifier modifier = Modifier.createModifier(parts[5], getPersonality());
		
		if (diceCount < 1) {
			throw getPersonality().getException("Roll0Dice");
		} else if (diceType < 1) {
			throw getPersonality().getException("Roll0Sides");
		} else if (diceType == 1) {
			long modified = modifier.apply(1 * diceCount); 
			throw getPersonality().getException("OneSidedDice", new TokenSubstitution("%DICECOUNT%", diceCount), 
					new TokenSubstitution("%MODIFIEDVALUE%", modified));
		}

		BehaviorsPair pair = Behavior.parseBehavior(coalesceBehavior(parts[4], parts[6]), diceType);
        Reroll reroll = pair.reroll;
        Explosion explosion = pair.explosion;
		String annotation = getAnnotationString(parts[7]);
		
		Roll roll = new Roll(diceCount, diceCount, new SimpleDie(diceType), modifier, reroll, explosion, getPersonality());
		List<GroupResult> groups = roll.performRoll(groupCount, random, statistics);
			
		String behaviors = Behavior.getPrettyString(roll.getReroll(), roll.getExplosion());
		
		String textKey;
        String natural;
        String modified;
        String criticalType = null;
        String criticalComment = null;
        if (groups.size() > 1) {
            textKey = "StandardMoreGroups";
            natural = buildNaturalList(groups);
            modified = buildModifiedList(groups);
        } else {
            GroupResult group = groups.get(0);
            
            if (group.isCriticalFailure() || group.isCriticalSuccess()) {
                textKey = "Standard1GroupCrit";
                
                if (group.isCriticalFailure()) {
                    criticalType = "FAILURE";
                    criticalComment = getPersonality().chooseCriticalFailureLine();
                } else if (group.isCriticalSuccess()) {
                    criticalType = "SUCCESS";
                    criticalComment = getPersonality().chooseCriticalSuccessLine();
                }
                
            } else {
               textKey = "Standard1Group";
            }
            
            natural = Long.toString(group.getNatural());
            modified = Long.toString(group.getModified());
        }
         
        return getPersonality().getRollResult(textKey, new TokenSubstitution("%GROUPCOUNT%", groupCount),
                new TokenSubstitution("%DICECOUNT%", diceCount), new TokenSubstitution("%DICETYPE%", diceType),
                new TokenSubstitution("%MODIFIER%", modifier), new TokenSubstitution("%BEHAVIORS%", behaviors), 
                new TokenSubstitution("%USER%", user), new TokenSubstitution("%CRITICALTYPE%", criticalType),
                new TokenSubstitution("%CRITICALCOMMENT%", criticalComment), new TokenSubstitution("%ANNOTATION%", annotation),
                new TokenSubstitution("%NATURALVALUE%", natural), new TokenSubstitution("%MODIFIEDVALUE%", modified));
	}
	
	/**
	 * Gets the description of the roller.
	 * @return the description.
	 */
	protected static String getDesc() {
		StringBuilder builder = new StringBuilder();
		builder.append("A standard dice roller, that can handle X number of dice of Y sides each, in the format XdY. (ex. 2d6). ");
		builder.append("Positive or negative modifiers may be applied to affect the result (ex. 2d6-5). If rolling only one die, the initial number may be omitted (ex. d20+10). ");
		builder.append("To roll additional groups of die, prefix the roll with a number then a space (ex. 10 d20+10). Modifiers will be applied to each group individually. ");
		builder.append("Brutal values of 1-9 are available by adding \"b\" then a number (ex. 2d8b2+5). Vorpal is also available by appending \"v\" (ex. 2d8v+5).");
		return builder.toString();
	}
	
	protected static String getRegexp() {
	    return TOTAL_REGEX;
	}
}