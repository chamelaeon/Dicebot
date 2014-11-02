package com.chamelaeon.dicebot.rollers;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.DieResult;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.dice.Modifier;
import com.chamelaeon.dicebot.dice.Roll;
import com.chamelaeon.dicebot.dice.SimpleDie;
import com.chamelaeon.dicebot.random.Random;

/** A roller for handling White Wolf die behavior. */
public class WhiteWolfRoller extends Roller {
	
	/** Regex piece for the basic roll. */
    private static final String BASIC_ROLL_REGEX = "(\\d+)t(\\d+)";
    /** Regex piece for the specialization. */
    private static final String SPECIALIZATION_REGEX = "(e)?";
    /** Regex piece for the DC. */
    private static final String DC_REGEX = "([ ]*[dc|DC]+(\\d+))?";
    /** The complete regex for the roller. */
    private static final String TOTAL_REGEX = "^" + BASIC_ROLL_REGEX + SPECIALIZATION_REGEX + MODIFIER_REGEX + SPECIALIZATION_REGEX + DC_REGEX + "$";
    
	/**
	 * Constructor.
	 * @param personality The object containing the dicebot personality.
	 */
	public WhiteWolfRoller(Personality personality) {
		super(TOTAL_REGEX, "White Wolf", getDesc(), 
		        Arrays.asList("Basic roll: 7t2", "Roll with 2 fixed successes: 7t2+2", 
		                "Roll with 10s counting twice: 7t2e", "Roll with fixed successes and 10s counting twice: 7t2+2e",
		                "Roll with custom DC: 7t2 dc7", "Roll with fixed successes, 10s counting twice and a custom DC: 7t2+2e dc7"),
				personality);
	}
	
	/**
	 * Protected Constructor for testing.
	 * @param personality The object containing the dicebot personality.
	 * @param random The random for testing.
	 */
	WhiteWolfRoller(Personality personality, Random random) {
		super(TOTAL_REGEX, "White Wolf", getDesc(), 
		        Arrays.asList("Basic roll: 7t2", "Roll with 2 fixed successes: 7t2+2", 
		                "Roll with 10s counting twice: 7t2e", "Roll with fixed successes and 10s counting twice: 7t2+2e",
		                "Roll with custom DC: 7t2 dc7", "Roll with fixed successes, 10s counting twice and a custom DC: 7t2+2e dc7"),
				personality, random);
	}
	
	@Override
	public String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
		short rolled = getPersonality().parseShort(parts[1]);
		short neededSuccesses = getPersonality().parseShort(parts[2]);
		Modifier modifier = Modifier.createModifier(parts[4], getPersonality());
		String specialization = coalesceBehavior(parts[3], parts[5]);
		String dcString = StringUtils.defaultString(parts[6], " ");
		Short dc = 6;
		if (!StringUtils.isBlank(dcString.trim())) {
			dc = getPersonality().parseShort(parts[7]);
			dcString = " " + dcString.trim() + " ";
		}
		
		if (rolled < 1) {
			throw getPersonality().getException("Roll0Dice");
		} else if (dc <=0) {
			throw getPersonality().getException("DCEquals0");
		} else if (rolled < neededSuccesses) {
			throw getPersonality().getException("CannotSatisfySuccesses", 
			        new TokenSubstitution("%SUCCESSESNEEDED%", neededSuccesses), 
			        new TokenSubstitution("%DICEROLLED%", rolled));
		}
		
		Roll roll = new Roll(rolled, rolled, new SimpleDie((short) 10), modifier, null, null, getPersonality());
		List<GroupResult> groups = roll.performRoll(1, random, statistics);
		long successesOverMinimum = -neededSuccesses;
        int onesRolled = 0;
        for (DieResult die : groups.get(0).getDice()) {
        
            if (10 == die.getResult() && !StringUtils.isEmpty(specialization)) {
                successesOverMinimum += 2;
            } else if (die.getResult() >= dc) {
                successesOverMinimum++;
            } else if (die.getResult() == 1) {
                onesRolled++;
            }
        }
        successesOverMinimum = modifier.apply(successesOverMinimum);
        
        String textKey;
        if (successesOverMinimum >= 0) {
            textKey = "WhiteWolfSuccess";
        } else {
            textKey = "WhiteWolfFailure";
        }
        
        return getPersonality().getRollResult(textKey,
            new TokenSubstitution("%ROLLEDDICE%", rolled), new TokenSubstitution("%SUCCESSESNEEDED%", neededSuccesses), 
            new TokenSubstitution("%MODIFIER%", modifier), new TokenSubstitution("%SPECIALIZATION%", specialization), 
            new TokenSubstitution("%DCSTRING%", dcString), new TokenSubstitution("%USER%", user), 
            new TokenSubstitution("%DICEVALUE%", groups.get(0).getDice()), new TokenSubstitution("%ONESROLLED%", onesRolled), 
            new TokenSubstitution("%SUCCESSESOVERMINIMUM%", successesOverMinimum));
	}
	
	/**
     * Gets the description of the roller.
     * @return the description.
     */
	public static String getDesc() {
		StringBuilder builder = new StringBuilder();
		builder.append("A dice roller for White Wolf (roll/successes style), which rolls a number of d10s and looks for numbers over a certain value, ");
		builder.append("attempting to accumulate a certain number of successes. A number of fixed successes can be added or removed. Specifying e (for ");
		builder.append("emphasis) makes 10s explode twice. An example: 6t2+1e dc7 - this specifies rolling 6 dice, looking for 2 dice with a value of ");
		builder.append("7 or higher. Tens will explode twice, and there will be one guaranteed success. If not specified, the DC is 6+.");
		return builder.toString();
	}
	
	protected static String getRegexp() {
		return TOTAL_REGEX;
	}
}