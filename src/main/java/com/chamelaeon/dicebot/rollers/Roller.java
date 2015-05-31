package com.chamelaeon.dicebot.rollers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;
import com.chamelaeon.dicebot.personality.BasicPersonality;
import com.chamelaeon.dicebot.random.MersenneTwisterRandom;
import com.chamelaeon.dicebot.random.Random;

/** Abstract class describing all types of rollers. */
public abstract class Roller extends DicebotListenerAdapter {
	/** Regex piece for group matching. */
	protected static final String GROUP_REGEX = "(\\d+ )?";
	/** Regex piece for modifier matching. */
	protected static final String MODIFIER_REGEX = "(\\+\\d+|-\\d+)?";
	/** Regex piece for annotation matching. */
	protected static final String ANNOTATION_REGEX = "( [\\w ,-\\.\"!']+)?";
	/** The random to use. */
	protected final Random random;
	/** The personality object containing quotes (if necessary). */
	private final Personality personality;
	
	/** Protected constructor. */
	protected Roller(String regexp, String name, String description, List<String> examples, Personality personality) {
	    this(regexp, name, description, examples, personality, new MersenneTwisterRandom());
	}
	
	/** Super-Protected constructor. */
    Roller(String regexp, String name, String description, List<String> examples, Personality personality, Random random) {
		super(regexp, new HelpDetails(name, description, "roller", examples));
		this.random = random;
		this.personality = personality;
	}
	
	/** Gets the {@link BasicPersonality} object. */
	protected Personality getPersonality() {
		return personality;
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) throws InputException {
		event.respondWithAction(
		        assembleRoll(groups.toArray(new String[groups.size()]), event.getUser().getNick(), 
		                event.getBot().getStatistics()));
	}
	
	/**
	 * Parses the groups portion of the roll and limits it to the range [1, 10].
	 * @param groupString The groups string to parse.
	 * @return the number of groups.
	 * @throws InputException if the groups could not be parsed or is less than 1.
	 */
	protected short parseGroups(String groupString) throws InputException {
		if (null != groupString) {
			short parsedGroups = getPersonality().parseShort(groupString);
			if (parsedGroups >= 1) {
				if (parsedGroups > 10) {
					return 10;
				} else {
					return parsedGroups;
				}
			} else {
				throw getPersonality().getException("LessThanOneGroup");
			}
		} else {
			return 1;
		}
	}
	
	protected String buildModifiedList(List<GroupResult> groups) {
        StringBuilder modified = new StringBuilder();
        for (GroupResult group : groups) {
            modified.append(group.getModified());
            modified.append(" ");
        }
        return modified.toString();
    }

	protected String buildNaturalList(List<GroupResult> groups) {
        StringBuilder natural = new StringBuilder();
        for (GroupResult group : groups) {
            natural.append(group.getNatural());
            natural.append(" ");
        }
        return natural.toString();
    }
	
	protected String getAnnotationString(String part) {
	    if (StringUtils.isEmpty(part) || StringUtils.isEmpty(part.trim())) {
	        return "";
	    } else {
	        return " [" + part.trim() + "]";
	    }
	}
	
	/** 
	 * Takes two behavior strings and de-duplicates them into a single combined behavior string. If neither the left or right hand sides 
	 * exist, an empty string is returned.
	 * @param behaviorLeft The left-hand side behavior string.
	 * @param behaviorRight The right-hand side behavior string.
	 * @return the behavior string to use.
	 */
	protected String coalesceBehavior(String behaviorLeft, String behaviorRight) {
		behaviorLeft = StringUtils.defaultString(behaviorLeft, "");
		behaviorRight = StringUtils.defaultString(behaviorRight, "");
		
		// Take the left-hand string as canonical and see if we can remove anything from the right-hand side.
		for (int i = 0; i < behaviorRight.length(); i++) {
			String singleCharString = behaviorRight.substring(i, i + 1);
			String doubleCharString = "";
			if (i + 1 < behaviorRight.length()) {
				doubleCharString = behaviorRight.substring(i, i + 2);
			}

			// Check the double-char string first. If it exists, remove it and increment again to account for two chars.
			if (StringUtils.isNotEmpty(doubleCharString) && behaviorLeft.contains(doubleCharString)) {
				behaviorRight = behaviorRight.replace(doubleCharString, "");
				i++;
			} else if (behaviorLeft.contains(singleCharString)) {
				// Then the single-char string...
				behaviorRight = behaviorRight.replace(singleCharString, "");
			}
		}
		
		return behaviorLeft + behaviorRight;
	}
	
	/**
	 * Performs the actual roll, given all the matched groups from the parsing regexp.
	 * @param parts The parts to parse.
	 * @param user The user who made the roll.
	 * @param statistics The statistics object to use to track the roll.
	 * @return the result of the roll.
	 * @throws InputException if the input has issues.
	 */
	protected abstract String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException;
}