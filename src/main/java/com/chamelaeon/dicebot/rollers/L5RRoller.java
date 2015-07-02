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
import com.chamelaeon.dicebot.dice.behavior.Emphasis;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.L5RExplosion;
import com.chamelaeon.dicebot.dice.behavior.Mastery;
import com.chamelaeon.dicebot.dice.behavior.Raw;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.dice.behavior.Behavior.BehaviorsPair;

/** A roller for handling L5R die behavior, like "7k3" or "2k1+2". */
public class L5RRoller extends Roller {
	
	/** Regex piece for the basic roll. */
    private static final String BASIC_ROLL_REGEX = "(\\d+)k(\\d+)";
    /** Regex piece for the behavior. */
    private static final String BEHAVIOR_REGEX = "(me|em|er|re|e|m|r)?";
    /** Regex piece for the analyze flag. */
    private static final String ANALYZE_REGEX = "( a$)?";
    /** The complete regex for the roller. */
    private static final String TOTAL_REGEX = "^" + GROUP_REGEX + BASIC_ROLL_REGEX + BEHAVIOR_REGEX + MODIFIER_REGEX + BEHAVIOR_REGEX 
    		+ ANALYZE_REGEX + ANNOTATION_REGEX + "$";
	
    private static final double[][] normalRolls = {
        { 06.11 },
        { 08.32, 12.22 },
        { 09.63, 15.30, 18.33 },
        { 10.57, 17.39, 21.91, 24.44 },
        { 11.29, 18.96, 24.51, 28.34, 30.55 },
        { 11.88, 20.22, 26.53, 31.33, 34.68, 36.66 },
        { 12.38, 21.29, 28.20, 33.71, 37.96, 40.97, 42.77 },
        { 12.80, 22.20, 29.62, 35.70, 40.65, 44.48, 47.21, 48.88 },
        { 13.17, 23.04, 30.86, 37.41, 42.92, 47.42, 50.92, 53.42, 54.99 },
        { 13.48, 23.78, 31.98, 38.92, 44.88, 49.93, 54.07, 57.29, 59.61, 61.10 }
    };
    
    private static final double[][] emphasisRolls = {
        { 06.62 },
        { 08.75, 13.25 },
        { 10.08, 16.17, 19.87 },
        { 11.07, 18.18, 23.24, 26.49 },
        { 11.87, 19.73, 25.72, 30.16, 33.11 },
        { 12.55, 21.01, 27.68, 32.99, 37.00, 39.73 },
        { 13.14, 22.12, 29.31, 35.28, 40.10, 43.78, 46.36 },
        { 13.68, 23.10, 30.73, 37.20, 42.66, 47.09, 50.53, 52.98 },
        { 14.16, 23.98, 31.98, 38.68, 44.83, 49.87, 54.01, 57.24, 59.60 },
        { 14.60, 24.80, 33.11, 40.37, 46.74, 52.28, 56.99, 60.88, 63.95, 66.23 }
    };
    
    private static final double[][] masteryRolls = {
        { 06.88 },
        { 09.73, 13.75 },
        { 11.62, 17.57, 20.62 },
        { 13.05, 20.37, 24.96, 27.50 },
        { 14.21, 22.60, 28.32, 32.17, 34.37 },
        { 15.20, 24.50, 31.08, 35.90, 39.27, 41.24 },
        { 16.05, 26.15, 33.45, 39.05, 43.32, 46.32, 48.12 },
        { 16.79, 27.62, 35.56, 41.79, 46.76, 50.60, 53.31, 55.00 },
        { 17.46, 28.93, 37.46, 44.24, 49.78, 54.29, 57.80, 60.31, 61.88 },
        { 18.06, 30.14, 39.20, 46.47, 52.51, 57.58, 61.70, 64.93, 67.24, 68.74 }
    };
    
    private static final double[][] emphasisMasteryRolls = {
        { 07.46 },
        { 10.27, 14.92 },
        { 12.16, 18.66, 22.38 },
        { 13.60, 21.42, 26.59, 29.85 },
        { 14.77, 23.67, 29.89, 34.36, 37.31 },
        { 15.77, 25.59, 32.65, 38.02, 42.04, 44.76 },
        { 16.62, 27.25, 35.04, 41.13, 45.98, 49.68, 52.23 },
        { 17.37, 28.73, 37.16, 43.88, 49.36, 53.81, 57.24, 59.71 },
        { 18.04, 30.06, 39.10, 46.34, 52.38, 57.44, 61.59, 64.81, 67.17 },
        { 18.64, 31.29, 40.87, 48.61, 55.10, 60.67, 65.39, 69.29, 72.35, 74.62 }
    };
    
    private static final double[][] rawRolls = {
        { 05.50 },
        { 07.15, 11.00 },
        { 07.97, 13.47, 16.50 },
        { 08.47, 14.97, 19.47, 22.00 },
        { 08.79, 15.96, 21.46, 25.29, 27.50 },
        { 09.02, 16.66, 22.88, 27.67, 31.02, 33.00 },
        { 09.19, 17.19, 23.94, 29.44, 33.69, 36.89, 38.50 },
        { 09.32, 17.60, 24.77, 30.82, 35.76, 39.60, 42.33, 44.00 },
        { 09.43, 17.92, 25.43, 31.92, 37.43, 41.92, 45.42, 47.92, 49.50 },
        { 09.51, 18.19, 25.96, 32.82, 38.78, 43.83, 47.96, 51.19, 53.51, 55.00 }
    };
    
    private static final double[][] emphasisRawRolls = {
        { 05.95 },
        { 07.45, 11.20 },
        { 08.20, 14.15, 17.85 },
        { 08.64, 15.51, 20.56, 23.80 },
        { 08.94, 16.41, 22.36, 26.80, 29.75 },
        { 09.15, 17.05, 22.65, 28.96, 32.97, 35.70 },
        { 09.30, 17.53, 24.62, 30.58, 35.39, 39.08, 41.65 },
        { 09.42, 17.90, 25.37, 31.82, 37.28, 41.71, 45.15, 47.60 },
        { 09.51, 18.19, 25.96, 32.83, 38.78, 43.83, 47.96, 51.20, 53.55 },
        { 09.58, 18.43, 26.45, 33.65, 40.01, 45.56, 50.28, 54.17, 57.22, 59.50 }
    };
    
	/**
	 * Constructor.
	 * @param personality The object containing the dicebot personality.
	 */
	public L5RRoller(Personality personality) {
		super(TOTAL_REGEX, "L5R", getDesc(),
		        Arrays.asList("Basic roll: 5k2", "Roll with modifier: 7k3+5", "Roll with rollover: 14k6", 
		                "Roll with emphasis: 9k2e", "Roll with modifier and emphasis: 10k6+16e", 
		                "Roll with mastery: 9k3m", "Roll with modifier, emphasis, and mastery: 10k6me",
		                "Roll with no explosions (raw): 9k2r", "Roll with no explosions (raw) and emphasis: 9k2r", 
		                "Roll with analysis: 9k2me+3 a", "One with everything: 10k8+16me"),
				personality);
	}
	
	@Override
	public String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
		int groupCount = parseGroups(parts[1]);
		short rolled = getPersonality().parseShort(parts[2]);
		short kept = getPersonality().parseShort(parts[3]);
		Modifier modifier = Modifier.createModifier(parts[5], getPersonality());
		
		// We use 100 as a default since it's a SWAG at the max value of a die...
		BehaviorsPair pair = Behavior.parseBehavior(coalesceBehavior(parts[4], parts[6]), 100);
		Reroll reroll = pair.reroll;
		Explosion explosion = pair.explosion;
		String annotation = getAnnotationString(parts[8]);
		
		// If we have no special explosion use the default L5R one. 
		if (null == explosion) {
			explosion = new L5RExplosion();
		}
		
		Roll roll = handleRollover(new Roll(rolled, kept, new SimpleDie((short) 10), modifier, reroll, explosion, getPersonality()));
		if (roll.getKept() < 1) {
			throw getPersonality().getException("KeepingLessThan1");
		} else if (roll.getRolled() < roll.getKept()) {
			throw getPersonality().getException("RollLessThanKeep");
		}
		
		// If the analyze flag is on, analyze the roll. Otherwise perform it.
		if (null != parts[7]) {
			return analyzeRoll(roll, user);
		} else {
			List<GroupResult> groups = roll.performRoll(groupCount, random, statistics);
			String behaviors = Behavior.getPrettyString(roll.getReroll(), roll.getExplosion());
			
			String textKey;
			String natural;
			String modified;
			if (groups.size() > 1) {
                natural = buildNaturalList(groups);
                modified = buildModifiedList(groups);
                textKey = "L5RMoreGroups";
            } else {
                GroupResult group = groups.get(0);
                textKey = "L5ROneGroup";
                natural = group.getDice().toString();
                modified = Long.toString(group.getModified());
            }
			
			// ALWAYS use the roll values for rolled, kept, and modifier because it has been adjusted for rollover.
			return getPersonality().getRollResult(textKey, 
			        new TokenSubstitution("%GROUPCOUNT%", groupCount), new TokenSubstitution("%ROLLEDDICE%", roll.getRolled()), 
			        new TokenSubstitution("%KEPTDICE%", roll.getKept()), new TokenSubstitution("%MODIFIER%", roll.getModifier()), 
			        new TokenSubstitution("%BEHAVIORS%", behaviors), new TokenSubstitution("%USER%", user), 
			        new TokenSubstitution("%NATURALVALUE%", natural), new TokenSubstitution("%MODIFIEDVALUE%", modified),
			        new TokenSubstitution("%ANNOTATION%", annotation));
		}
	}
	
	/**
	 * Handles the rollover for a group, returning a group which is guaranteed
	 * to be no more than 10k10.
	 * @param roll The group to handle rollover for.
	 * @return the rollable group.
	 * @throws InputException if the rolled-over values can't meet the reroll condition.
	 */
	private Roll handleRollover(Roll roll) throws InputException {
		if (roll.getRolled() >= 10 && roll.getKept() >= 10) {
			int rolledOverflow = roll.getRolled() - 10;
			int keptOverflow = roll.getKept() - 10;
			int overflow = rolledOverflow + keptOverflow;

			return roll.alterValues(10, 10, roll.getModifier().appendToValue(2 * overflow));
		}
		
		if (roll.getRolled() > 10) {
			int overflow = roll.getRolled() - 10;
			if (overflow > 1) {
				// We have overflow. Remove a single die and recursively call.
				return handleRollover(roll.alterValues(roll.getRolled() - 2, roll.getKept() + 1, roll.getModifier()));
			} else {
				// Strip off the extra 1 and return.
				return roll.alterValues(10, roll.getKept(), roll.getModifier());
			}
		}
			
		return roll;
	}
	
	/**
	 * Finds the mean average for the given roll for the given user, using the rolled/kept pair, the behavior, and the modifier
	 * to determine it.
	 * @param roll The roll to analyze.
	 * @param user The user to analyze the roll for.
	 * @return the analysis result string.
	 */
	private String analyzeRoll(Roll roll, String user) {
	    Explosion explosion = roll.getExplosion();
	    Reroll reroll = roll.getReroll();
	    Modifier mod = roll.getModifier();
	    
	    double[][] sourceArray;
	    if (reroll instanceof Emphasis && explosion instanceof Mastery) {
	        sourceArray = emphasisMasteryRolls;
	    } else if (reroll instanceof Emphasis && explosion instanceof Raw) {
	        sourceArray = emphasisRawRolls;
	    } else if (reroll instanceof Emphasis) {
	        sourceArray = emphasisRolls;
	    } else if (explosion instanceof Mastery) {
	        sourceArray = masteryRolls;
	    } else if (explosion instanceof Raw) {
	        sourceArray = rawRolls;
	    } else {
	        sourceArray = normalRolls;
	    }
	    
	    String behaviors = Behavior.getPrettyString(roll.getReroll(), roll.getExplosion());
	    double mean = sourceArray[roll.getRolled() - 1][roll.getKept() -1];
	    mean = mod.apply(mean);
	    
	    String meanString = String.format("%.2f", mean);
	    
	    return getPersonality().getMessage("L5RAnalyze", new TokenSubstitution("%ROLLEDDICE%", roll.getRolled()), 
                new TokenSubstitution("%KEPTDICE%", roll.getKept()), new TokenSubstitution("%MODIFIER%", roll.getModifier()), 
                new TokenSubstitution("%BEHAVIORS%", behaviors), new TokenSubstitution("%USER%", user), 
                new TokenSubstitution("%AVERAGE%", meanString));
	}
	
	/**
     * Gets the description of the roller.
     * @return the description.
     */
	public static String getDesc() {
		StringBuilder builder = new StringBuilder();
		builder.append("A dice roller for Legend of the Five Rings (roll/keep style), which rolls X number of d10s and keeps Y of them (ex. 5k3). ");
		builder.append("Positive or negative modifiers may be applied to affect the result (ex. 5k3-5). Rolls that would \"roll over\" into static bonuses are automatically converted (ex. 13k9 into 10k10+2). ");
		builder.append("To roll additional groups of die, prefix the roll with a number then a space (ex. 10 2k2-5). Modifiers will be applied to each group individually. ");
		builder.append("Emphasis rolls are available by appending \"e\" to the roll (ex. 9k5e). Mastery is also available by appending \"m\" (ex. 12k3m). They may be combined (ex. 12k3+5em). ");
		builder.append("To analyze the predicted average of a given roll, put an 'a' after the roll, separated by a space (ex. 9k5e+2 a). This can help with knowing how many raises you should take.");
		return builder.toString();
	}
	
	protected static String getRegexp() {
		return TOTAL_REGEX;
	}
}