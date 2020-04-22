package com.chamelaeon.dicebot.rollers;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.dice.Modifier;
import com.chamelaeon.dicebot.dice.Roll;
import com.chamelaeon.dicebot.dice.SimpleDie;
import com.chamelaeon.dicebot.random.Random;

import java.util.Arrays;
import java.util.List;

/**
 * A roller for handling Powered by the Apocalypse-based systems, like Dungeon World or Urban Shadows.
 * @author Chamelaeon
 */
public class PbtARoller extends Roller {
	/** Regex piece for the basic roll. */
    private static final String BASIC_ROLL_REGEX = "dA";
    /** The complete regex for the roller. */
    private static final String TOTAL_REGEX = "^" + GROUP_REGEX + BASIC_ROLL_REGEX + MODIFIER_REGEX + "$";

	/**
	 * Constructor.
	 * @param personality The object containing the dicebot personality.
	 */
	public PbtARoller(Personality personality) {
	    super(TOTAL_REGEX, "Powered by the Apocalypse", getDesc(),
				Arrays.asList("Normal rolls: dA, which will roll 2d6", "Roll with modifier: dA+3"),
                personality);
	}

	/**
     * Protected Constructor for testing.
     * @param personality The object containing the dicebot personality.
     * @param random The random for testing.
     */
    PbtARoller(Personality personality, Random random) {
		super(TOTAL_REGEX, "Powered by the Apocalypse", getDesc(),
				Arrays.asList("Normal rolls: dA, which will roll 2d6", "Roll with modifier: dA+3"),
				personality, random);
	}

	@Override
	public String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
		short groupCount = parseGroups(parts[1]);
		Modifier modifier = Modifier.createModifier(parts[2], getPersonality());

		Roll roll = new Roll((short) 2, (short) 2, new SimpleDie((short) 6), modifier, null, null, getPersonality());
		List<GroupResult> groups = roll.performRoll(groupCount, random, statistics);

		String textKey;
        String natural;
        String modified;
        String rollType = "";
        String rollComment = "";
        if (groups.size() > 1) {
            textKey = "PbtAMoreGroups";
            natural = buildNaturalList(groups);
            modified = buildModifiedList(groups);
        } else {
            GroupResult group = groups.get(0);

            if (group.getModified() > 9) {
				textKey = "PbtA1GroupStrongHit";
				rollType = "pbtaStrongHit";
			} else if (group.getModified() > 6) {
				textKey = "PbtA1GroupHit";
				rollType = "pbtaHit";
            } else {
                textKey = "PbtA1GroupMiss";
				rollType = "pbtaMiss";
            }

			if (getPersonality().shouldShowMessagesForRollResultType(rollType)) {
				textKey += "WithMessage";
				rollComment = getPersonality().chooseRollResultTypeCommentLine(rollType);
			}

            natural = Long.toString(group.getNatural());
            modified = Long.toString(group.getModified());
        }

        return getPersonality().getRollResult(textKey, new TokenSubstitution("%GROUPCOUNT%", groupCount),
                new TokenSubstitution("%MODIFIER%", modifier), new TokenSubstitution("%USER%", user),
				new TokenSubstitution("%ROLLTYPE%", rollType), new TokenSubstitution("%ROLLCOMMENT%", rollComment),
                new TokenSubstitution("%NATURALVALUE%", natural), new TokenSubstitution("%MODIFIEDVALUE%", modified));
	}

	/**
	 * Gets the description of the roller.
	 * @return the description.
	 */
	protected static String getDesc() {
		return "A dice roller for games Powered by the Apocalypse (e.g. Apocalypse World, Dungeon World, Urban Shadows, and so on). You roll using the format `dA`, " +
				"which always rolls 2d6. Positive or negative modifiers may be applied to affect the result (ex. dA-1). " +
				"To roll additional groups of die, prefix the roll with a number then a space (ex. 10 dA+3). Modifiers will be applied to each group individually. " +
				"The result will indicate a miss, a hit, or a strong hit where appropriate. Since PbtA games vary widely, it won't handle hold or anything specific to a move.";
	}

	protected static String getRegexp() {
	    return TOTAL_REGEX;
	}
}
