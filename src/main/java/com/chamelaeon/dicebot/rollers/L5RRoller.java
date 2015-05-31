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
import com.chamelaeon.dicebot.dice.behavior.L5RExplosion;
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
	
	/**
	 * Constructor.
	 * @param personality The object containing the dicebot personality.
	 */
	public L5RRoller(Personality personality) {
		super(TOTAL_REGEX, "L5R", getDesc(),
		        Arrays.asList("Basic roll: 5k2", "Roll with modifier: 7k3+5", "Roll with rollover: 14k6", 
		                "Roll with emphasis: 9k2e", "Roll with modifier and emphasis: 10k6+16e", 
		                "Roll with mastery: 9k3m", "Roll with modifier, emphasis, and mastery: 10k6me",
		                "Roll with no explosions (raw): 9k2r", "One with everything: 10k8+16me"),
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
			return analyzeRoll(roll);
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
	
	private String analyzeRoll(Roll roll) {
		//Regular - 1k1: 6, 1k0: 2, 0k1: 4
		//Emphasis - 1k1: 6.6, 1k0: 2.1, 0k1: 4.3
		//Mastery adds about .5 per 1k1
		return "doesn't quite know how to analyze rolls yet.";
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
		builder.append("Emphasis rolls are available by appending \"e\" to the roll (ex. 9k5e). Mastery is also available by appending \"m\" (ex. 12k3m). They may be combined (ex. 12k3+5em).");
		return builder.toString();
	}
	
	protected static String getRegexp() {
		return TOTAL_REGEX;
	}
}