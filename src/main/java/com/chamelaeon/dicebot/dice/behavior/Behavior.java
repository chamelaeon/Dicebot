/**
 * 
 */
package com.chamelaeon.dicebot.dice.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.chamelaeon.dicebot.api.InputException;

/**
 * Represents a dice behavior. This can be something like "Explodes on 10s" or
 * "reroll on ones". 
 *  
 * @author Chamelaeon
 */
public abstract class Behavior {
	/** The map of behavior flags to behaviors. */
	private static final Map<Character, BehaviorFactory> BEHAVIORS = new HashMap<Character, BehaviorFactory>();
	
	// Provide the existing behaviors with their string keys.
	static {
	    BEHAVIORS.put('e', Emphasis.getFactory());
	    BEHAVIORS.put('b', Brutal.getFactory());
	    BEHAVIORS.put('m', Mastery.getFactory());
	    BEHAVIORS.put('v', Vorpal.getFactory());
	    BEHAVIORS.put('r', Raw.getFactory());
	}
	
	/** The range for the reroll. */
    private final Integer range; 
	
    /**
     * 
     * @param range
     */
    protected Behavior(Integer range) {
        this.range = range;
    }
    
    /**
     * Gets the threshold for the reroll.
     * @return the threshold value.
     */
    public Integer getThreshold() { 
       return range;
    }
    
    public static BehaviorsPair parseBehavior(String behaviorString, int diceType) throws InputException {
        BehaviorsPair pair = new BehaviorsPair();
        if (!StringUtils.isEmpty(behaviorString)) {
            
            // Process the string looking for valid markers.
            List<Reroll> rerolls = new ArrayList<Reroll>();
            List<Explosion> explosions = new ArrayList<Explosion>();
            char[] characters = behaviorString.toCharArray();
            for (int i = 0; i < characters.length; i++) {

                // Get the threshold for the behavior, if any.
                int threshold = diceType;
                Character currentChar = characters[i];
                Character nextChar = characters.length > i + 1 ? characters[i + 1] : ' ';
                Character nextNextChar = characters.length > i + 2 ? characters[i + 2] : ' ';
                if (Character.isDigit(nextChar) && Character.isDigit(nextNextChar)) {
                    String val = new String(new char[] {nextChar, nextNextChar});
                    threshold = Integer.parseInt(val);
                } else if (Character.isDigit(nextChar)) {
                    threshold = Character.getNumericValue(nextChar);
                }
                
                if (BEHAVIORS.containsKey(currentChar)) {
                    Behavior behavior = BEHAVIORS.get(currentChar).createBehavior(threshold);
                    if (behavior instanceof Reroll) {
                        rerolls.add((Reroll) behavior);
                    } else if (behavior instanceof Explosion) {
                        explosions.add((Explosion) behavior);
                    }
                }
            }
            
            if (rerolls.size() > 0) {
                pair.reroll = rerolls.get(0);
            }
            
            if (explosions.size() > 0) {
                pair.explosion = explosions.get(0);
            }
        }
        
        return pair;
    }
    
	/**
	 * Gets a pretty string of all valid rerolls and explosions for a roll with the given
	 * reroll and explosion behavior.
	 * @param reroll The reroll behavior.
	 * @param explosion The explosion behavior.
	 * @return the pretty string.
	 */
	public static String getPrettyString(Reroll reroll, Explosion explosion) {
		StringBuilder builder = new StringBuilder();
		if (null != reroll) {
			builder.append(reroll);
		}
		if (null != explosion) {
			builder.append(explosion);
		}
		return builder.toString();
	}
	
	public static class BehaviorsPair {
	    public Reroll reroll;
	    public Explosion explosion;
	}
}
