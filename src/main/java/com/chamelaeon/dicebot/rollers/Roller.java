package com.chamelaeon.dicebot.rollers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.Die.FudgeDie;
import com.chamelaeon.dicebot.dice.Die.SimpleDie;
import com.chamelaeon.dicebot.dice.behavior.Behavior;
import com.chamelaeon.dicebot.dice.behavior.Behavior.BehaviorsPair;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.L5RExplosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.dice.DieResult;
import com.chamelaeon.dicebot.dice.GroupResult;
import com.chamelaeon.dicebot.dice.Modifier;
import com.chamelaeon.dicebot.dice.Roll;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;
import com.chamelaeon.dicebot.personality.BasicPersonality;
import com.chamelaeon.dicebot.random.MersenneTwisterRandom;
import com.chamelaeon.dicebot.random.Random;

/** Abstract class describing all types of rollers. */
public abstract class Roller extends DicebotListenerAdapter {
	/** The random to use. */
	protected final Random random;
	/** The personality object containing quotes (if necessary). */
	private final Personality personality;
	
	/** Protected constructor. */
	protected Roller(String regexp, String name, String description, List<String> examples, Personality personality) {
		super(regexp, new HelpDetails(name, description, "roller", examples));
		random = new MersenneTwisterRandom();
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
	
	/**
	 * Performs the actual roll, given all the matched groups from the parsing regexp.
	 * @param parts The parts to parse.
	 * @param user The user who made the roll.
	 * @param statistics The statistics object to use to track the roll.
	 * @return the result of the roll.
	 * @throws InputException if the input has issues.
	 */
	protected abstract String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException;
	
	/** A roller for handling standard die behavior, like "2d6" or "1d20". */
	public static class StandardRoller extends Roller {
		/**
		 * Constructor.
		 * @param personality The object containing the dicebot personality.
		 */
		public StandardRoller(Personality personality) {
			super("^(\\d+ )?(\\d*)d(\\d+)(b[1-9]|v(?:[1-9][0-9]?)?)?(\\+\\d+|-\\d+)?", "Standard", getDesc(),
			        Arrays.asList("Normal rolls: 2d6, d20", "Roll with modifier: d20+3", "Roll with Brutal 2: 2d6b2",
			                "Roll with basic vorpal: d10v", "Roll with expanded vorpal (5-10 range): d10v6",
			                "Roll with brutal 2 and modifier: 2d6b2+5", "Roll with vorpal and modifier: 2d6v+3"),
					personality);
		}
		
		@Override
		public String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
			short groupCount = parseGroups(parts[1]);
			short diceCount = getPersonality().parseDiceCount(parts[2]);
			short diceType = getPersonality().parseShort(parts[3]);
			if (diceCount < 1) {
				throw getPersonality().getException("Roll0Dice");
			} else if (diceType < 1) {
				throw getPersonality().getException("Roll0Sides");
			} else if (diceType == 1) {
				throw getPersonality().getException("OneSidedDice", new TokenSubstitution("%DICECOUNT%", diceCount));
			}

			BehaviorsPair pair = Behavior.parseBehavior(parts[4], diceType);
            Reroll reroll = pair.reroll;
            Explosion explosion = pair.explosion;
			Modifier modifier = Modifier.createModifier(parts[5], getPersonality());
			
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
	                new TokenSubstitution("%CRITICALCOMMENT%", criticalComment), 
	                new TokenSubstitution("%NATURALVALUE%", natural), new TokenSubstitution("%MODIFIEDVALUE%", modified));
		}
		
		/**
		 * Gets the description of the roller.
		 * @return the description.
		 */
		public static String getDesc() {
			StringBuilder builder = new StringBuilder();
			builder.append("A standard dice roller, that can handle X number of dice of Y sides each, in the format XdY. (ex. 2d6). ");
			builder.append("Positive or negative modifiers may be applied to affect the result (ex. 2d6-5). If rolling only one die, the initial number may be omitted (ex. d20+10). ");
			builder.append("To roll additional groups of die, prefix the roll with a number then a space (ex. 10 d20+10). Modifiers will be applied to each group individually. ");
			builder.append("Brutal values of 1-9 are available by adding \"b\" then a number (ex. 2d8b2+5). Vorpal is also available by appending \"v\" (ex. 2d8v+5).");
			return builder.toString();
		}
	}
	
	/** A roller for handling L5R die behavior, like "7k3" or "2k1+2". */
	public static class L5RRoller extends Roller {
		/**
		 * Constructor.
		 * @param personality The object containing the dicebot personality.
		 */
		public L5RRoller(Personality personality) {
			super("^(\\d+ )?(\\d+)k(\\d+)(\\+\\d+|\\-\\d+)?(me|em|e|m)?( a$)?", "L5R", getDesc(),
			        Arrays.asList("Basic roll: 5k2", "Roll with modifier: 7k3+5", "Roll with rollover: 14k6", 
			                "Roll with emphasis: 9k2e", "Roll with modifier and emphasis: 10k6+16e", 
			                "Roll with mastery: 9k3m", "Roll with modifier, emphasis, and mastery: 10k6me",
			                "One with everything: 10k8+16me"),
					personality);
		}
		
		@Override
		public String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
			int groupCount = parseGroups(parts[1]);
			short rolled = getPersonality().parseShort(parts[2]);
			short kept = getPersonality().parseShort(parts[3]);
			Modifier modifier = Modifier.createModifier(parts[4], getPersonality());
			
			// We use 100 as a default since it's a SWAG at the max value of a die...
			BehaviorsPair pair = Behavior.parseBehavior(parts[5], 100);
			Reroll reroll = pair.reroll;
			Explosion explosion = pair.explosion;
			
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
			if (null != parts[6]) {
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
				
				return getPersonality().getRollResult(textKey, 
				        new TokenSubstitution("%GROUPCOUNT%", groupCount), new TokenSubstitution("%ROLLEDDICE%", rolled), 
				        new TokenSubstitution("%KEPTDICE%", kept), new TokenSubstitution("%MODIFIER%", modifier), 
				        new TokenSubstitution("%BEHAVIORS%", behaviors), new TokenSubstitution("%USER%", user), 
				        new TokenSubstitution("%NATURALVALUE%", natural), new TokenSubstitution("%MODIFIEDVALUE%", modified));
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
				
			if (roll.getKept() > 10) {
				int overflow = roll.getKept() - 10;
				return roll.alterValues(roll.getRolled(), 10, roll.getModifier().appendToValue(2 * overflow));
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
	}
	
	/** A roller for handling White Wolf die behavior. */
	public static class WhiteWolfRoller extends Roller {
		/**
		 * Constructor.
		 * @param personality The object containing the dicebot personality.
		 */
		public WhiteWolfRoller(Personality personality) {
			super("^(\\d+)t(\\d+)(\\+\\d+|\\-\\d+)?(e)?([ ]*[dc|DC]+(\\d+))?", "White Wolf", getDesc(), 
			        Arrays.asList("Basic roll: 7t2", "Roll with 2 fixed successes: 7t2+2", 
			                "Roll with 10s counting twice: 7t2e", "Roll with fixed successes and 10s counting twice: 7t2+2e",
			                "Roll with custom DC: 7t2 dc7", "Roll with fixed successes, 10s counting twice and a custom DC: 7t2+2e dc7"),
					personality);
		}
		
		@Override
		public String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
			short rolled = getPersonality().parseShort(parts[1]);
			short neededSuccesses = getPersonality().parseShort(parts[2]);
			Modifier modifier = Modifier.createModifier(parts[3], getPersonality());
			String specialization = StringUtils.defaultString(parts[4]);
			String dcString = StringUtils.defaultString(parts[5], " ");
			Short dc = 6;
			if (!StringUtils.isBlank(dcString.trim())) {
				dc = Short.parseShort(parts[6]);
				dcString = " " + dcString + " ";
			}
			
			if (rolled < 1) {
				throw getPersonality().getException("Roll0Dice");
			} else if (dc < 0) {
				throw getPersonality().getException("DCLessThan0");
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

            if (null != modifier) {
                successesOverMinimum = modifier.apply(successesOverMinimum);
            }
            
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
                new TokenSubstitution("%SUCCESSES%", successesOverMinimum));
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
	}
	
	/** A roller for Fudge behavior, e.g. "4dF". */
	public static class FudgeRoller extends Roller {
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
			super("^(\\d+ )?(\\d+)d[fF](\\+\\d+|-\\d+)?", "Fudge",
					"A dice roller for the FUDGE dice style, which rolls X number of d6s with faces of ['-', '-', ' ', ' ', '+', '+'] and returns the additive result (ex. 4dF).",
					Arrays.asList("Basic roll: 4dF", " Roll with modifier: 4dF+3"),
					personality);
		}

		@Override
		protected String assembleRoll(String[] parts, String user, Statistics statistics) throws InputException {
			short groupCount = parseGroups(parts[1]);
			short rolled = getPersonality().parseShort(parts[2]);
			Modifier modifier = Modifier.createModifier(parts[3], getPersonality());
			
			if (rolled < 1) {
				throw getPersonality().getException("Roll0Dice");
			}
			
			Roll roll = new Roll(rolled, rolled, new FudgeDie(), modifier, null, null, getPersonality());
			List<GroupResult> groups = roll.performRoll(groupCount, random, statistics);
			
			String textKey;
			String natural;
			String modified;
			String descriptor = null;
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
                    new TokenSubstitution("%MODIFIEDVALUE%", modified), new TokenSubstitution("%DESCRIPTOR%", descriptor));
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
}