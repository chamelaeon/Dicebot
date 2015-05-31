/**
 * 
 */
package com.chamelaeon.dicebot.rollers;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.Die;
import com.chamelaeon.dicebot.dice.DieResult;
import com.chamelaeon.dicebot.dice.EdgeDie;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.dice.Modifier;
import com.chamelaeon.dicebot.dice.Roll;
import com.chamelaeon.dicebot.dice.SimpleDie;

/**
 * A roller that handles the Shadowrun dice system.
 * @author Chamelaeon
 */
public class ShadowrunRoller extends Roller {

	/** Regex piece for the basic roll. */
    private static final String BASIC_ROLL_REGEX = "(\\d+)s(\\d+)";
    /** Regex piece for the behavior. */
    private static final String BEHAVIOR_REGEX = "(e)?";
    /** The complete regex for the roller. */
    private static final String TOTAL_REGEX = "^" + BASIC_ROLL_REGEX + BEHAVIOR_REGEX + MODIFIER_REGEX + BEHAVIOR_REGEX + "$";
	
    /**
	 * Constructor.
	 * @param personality The object containing the dicebot personality.
	 */
	public ShadowrunRoller(Personality personality) {
		super(TOTAL_REGEX, "Shadowrun", getDesc(), Arrays.asList(""), personality);
	}

	@Override
	protected String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
		short rolled = getPersonality().parseShort(parts[1]);
		short neededSuccesses = getPersonality().parseShort(parts[2]);
		Modifier modifier = Modifier.createModifier(parts[4], getPersonality());
		String edge = coalesceBehavior(parts[3], parts[5]);
		
		if (rolled < 1) {
			throw getPersonality().getException("Roll0Dice");
		} else if (rolled < neededSuccesses && StringUtils.isEmpty(edge)) {
			// Let them try a useless roll if they have edge.
			throw getPersonality().getException("CannotSatisfySuccesses", 
			        new TokenSubstitution("%SUCCESSESNEEDED%", neededSuccesses), 
			        new TokenSubstitution("%DICEROLLED%", rolled));
		}
		
		Die die = null;
		if (!StringUtils.isEmpty(edge)) {
			die = new EdgeDie();
		} else {
			die = new SimpleDie((short) 6);
		}
		
		Roll roll = new Roll(rolled, rolled, die, modifier, null, null, getPersonality());
		List<GroupResult> groups = roll.performRoll(1, random, statistics);
        int onesRolled = 0;
        int successes = 0;
        for (DieResult dieResult : groups.get(0).getDice()) {
            if (dieResult.getResult() >= 5) {
                successes++;
            } else if (dieResult.getResult() == 1) {
                onesRolled++;
            }
        }
        
        long successesOverMinimum = modifier.apply(successes - neededSuccesses);
        boolean glitch = onesRolled >= Math.ceil((double) groups.get(0).getDice().size() / 2.0);
        boolean criticalGlitch = glitch && (successes == 0);
        
        String textKey;
        if (successesOverMinimum >= 0) {
        	if (glitch) {
        		textKey = "ShadowrunSuccessGlitch";
        	} else {
        		textKey = "ShadowrunSuccess";
        	}
        } else {
        	if (criticalGlitch) {
        		textKey = "ShadowrunFailureCriticalGlitch";        		
        	} else if (glitch) {
        		textKey = "ShadowrunFailureGlitch";
        	} else {
        		textKey = "ShadowrunFailure";
        	}
        }
        
        return getPersonality().getRollResult(textKey,
            new TokenSubstitution("%ROLLEDDICE%", rolled), new TokenSubstitution("%SUCCESSESNEEDED%", neededSuccesses), 
            new TokenSubstitution("%MODIFIER%", modifier), new TokenSubstitution("%EDGE%", edge), 
            new TokenSubstitution("%USER%", user), new TokenSubstitution("%DICEVALUE%", groups.get(0).getDice()), 
            new TokenSubstitution("%SUCCESSESOVERMINIMUM%", successesOverMinimum));
	}

	/**
     * Gets the description of the roller.
     * @return the description.
     */
	public static String getDesc() {
		StringBuilder builder = new StringBuilder();
		builder.append("");
		return builder.toString();
	}
	
	protected static String getRegexp() {
		return TOTAL_REGEX;
	}
}
