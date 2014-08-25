package com.chamelaeon.dicebot.rollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chamelaeon.dicebot.Behavior;
import com.chamelaeon.dicebot.Behavior.Explosion;
import com.chamelaeon.dicebot.Behavior.L5RExplosion;
import com.chamelaeon.dicebot.Behavior.Reroll;
import com.chamelaeon.dicebot.Dicebot;
import com.chamelaeon.dicebot.InputException;
import com.chamelaeon.dicebot.Modifier;
import com.chamelaeon.dicebot.Statistics;
import com.chamelaeon.dicebot.Utils;
import com.chamelaeon.dicebot.commands.HelpDetails;
import com.chamelaeon.dicebot.dice.Die.FudgeDie;
import com.chamelaeon.dicebot.dice.Die.SimpleDie;
import com.chamelaeon.dicebot.dice.DieResult;
import com.chamelaeon.dicebot.dice.Roll;
import com.chamelaeon.dicebot.dice.Roll.GroupResult;
import com.chamelaeon.dicebot.listener.DicebotGenericEvent;
import com.chamelaeon.dicebot.listener.DicebotListenerAdapter;
import com.chamelaeon.dicebot.personality.BasicPersonality;
import com.chamelaeon.dicebot.personality.Personality;
import com.chamelaeon.dicebot.random.Random;
import com.chamelaeon.dicebot.random.Random.MersenneTwisterRandom;

/** Abstract class describing all types of rollers. */
public abstract class Roller extends DicebotListenerAdapter {
	/** The random to use. */
	protected final Random random;
	/** The personality object containing quotes (if necessary). */
	private final Personality personality;
	
	/** Protected constructor. */
	protected Roller(String regexp, String name, String description, 
			Statistics statistics, Personality personality) {
		super(regexp, new HelpDetails(name, description));
		random = new MersenneTwisterRandom(statistics);
		this.personality = personality;
	}
	
	/** Gets the {@link Statistics} object. */
	protected Statistics getStatistics() {
		return random.getStatistics();
	}
	
	/** Gets the {@link BasicPersonality} object. */
	protected Personality getPersonality() {
		return personality;
	}

	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) throws InputException {
		event.respondWithAction(assembleRoll(groups.toArray(new String[groups.size()]), 
		        event.getUser().getNick()));
	}
	
	/**
	 * Parses the groups portion of the roll and limits it to the range [1, 10].
	 * @param groupString The groups string to parse.
	 * @return the number of groups.
	 * @throws InputException if the groups could not be parsed or is less than 1.
	 */
	protected short parseGroups(String groupString) throws InputException {
		if (null != groupString) {
			short parsedGroups = Utils.parseShort(groupString, getPersonality());
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
	
	/**
	 * Prints the group count to a string, suppressing if the value is 1.
	 * @param groups The group count that was rolled.
	 * @return the string.
	 */
	protected String getGroupCountString(int groupCount) {
		String ret = "";
		if (groupCount > 1) {
			ret = ret + groupCount + " ";
		}
		return ret;
	}
	
	/**
	 * Performs the actual roll, given all the matched groups from the parsing regexp.
	 * @param parts The parts to parse.
	 * @param user The user who made the roll.
	 * @return the result of the roll.
	 * @throws InputException if the input has issues.
	 */
	protected abstract String assembleRoll(String[] parts, String user) throws InputException;
	
	/** A roller for handling standard die behavior, like "2d6" or "1d20". */
	public static class StandardRoller extends Roller {
		/**
		 * Constructor.
		 * @param statistics The statistics object for tracking statistics.
		 * @param personality The object containing the dicebot personality.
		 */
		public StandardRoller(Statistics statistics, Personality personality) {
			super("^(\\d+ )?(\\d*)d(\\d+)(b[1-9]|v(?:[1-9][0-9]?)?)?(\\+\\d+|-\\d+)?", "Standard", getDesc(), 
					statistics, personality);
		}
		
		@Override
		public String assembleRoll(String[] parts, String user) throws InputException {
			short groupCount = parseGroups(parts[1]);
			short diceCount = Utils.parseDiceCount(parts[2], getPersonality());
			short diceType = Utils.parseShort(parts[3], getPersonality());
			if (diceCount < 1) {
				throw getPersonality().getException("Roll0Dice");
			} else if (diceType < 1) {
				throw getPersonality().getException("Roll0Sides");
			} else if (diceType == 1) {
				throw getPersonality().getException("OneSidedDice", diceCount, diceCount);
			}
			Reroll reroll = Behavior.parseReroll(parts[4], getPersonality());
			Explosion explosion = Behavior.parseExplosion(parts[4], diceType, getPersonality());
			Modifier modifier = Modifier.createModifier(parts[5], getPersonality());
			
			Roll roll = new Roll(diceCount, diceCount, new SimpleDie(diceType), modifier, reroll, explosion, getPersonality());
			List<GroupResult> groups = roll.performRoll(groupCount, random, getStatistics());
				
			String behaviors = Behavior.getPrettyString(roll);
			return buildString(getGroupCountString(groupCount) + diceCount + "d" + diceType + behaviors + modifier , user, groups);
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

		/**
		 * Builds the result string for the roll groups.
		 * @param groups The groups to use to build the string.
		 * @return the output string.
		 */
		private String buildString(String baseRoll, String user, List<GroupResult> groups) {
			if (groups.size() > 1) {
				StringBuilder natural = new StringBuilder();
				for (GroupResult group : groups) {
					natural.append(group.getNatural());
					natural.append(" ");
				}
				
				StringBuilder modified = new StringBuilder();
				for (GroupResult group : groups) {
					modified.append(group.getModified());
					modified.append(" ");
				}
				return getPersonality().getRollResult("StandardMoreGroups", baseRoll, user, natural, modified);
			} else {
				GroupResult group = groups.get(0);
				if (group.isCriticalFailure() || group.isCriticalSuccess()) {
					if (group.isCriticalFailure()) {
						return getPersonality().getRollResult("Standard1GroupCrit", baseRoll, user, group.getNatural(), group.getModified(), "FAILURE", getPersonality().chooseCriticalFailureLine(random));
					} else if (group.isCriticalSuccess()) {
						return getPersonality().getRollResult("Standard1GroupCrit", baseRoll, user, group.getNatural(), group.getModified(), "SUCCESS", getPersonality().chooseCriticalSuccessLine(random));
					}
				} 
				return getPersonality().getRollResult("Standard1Group", baseRoll, user, group.getNatural(), group.getModified());
			}
		}
	}
	
	/** A roller for handling L5R die behavior, like "7k3" or "2k1+2". */
	public static class L5RRoller extends Roller {
		/**
		 * Constructor.
		 * @param statistics The statistics object for tracking statistics.
		 * @param personality The object containing the dicebot personality.
		 */
		public L5RRoller(Statistics statistics, Personality personality) {
			super("^(\\d+ )?(\\d+)k(\\d+)(\\+\\d+|\\-\\d+)?(me|em|e|m)?( a)?", "L5R", getDesc(), 
					statistics, personality);
		}
		
		@Override
		public String assembleRoll(String[] parts, String user) throws InputException {
			int groupCount = parseGroups(parts[1]);
			short rolled = Utils.parseShort(parts[2], getPersonality());
			short kept = Utils.parseShort(parts[3], getPersonality());
			Modifier modifier = Modifier.createModifier(parts[4], getPersonality());
			
			Reroll reroll = Behavior.parseReroll(parts[5], getPersonality());
			Explosion explosion = Behavior.parseExplosion(parts[5], getPersonality());
			
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
				List<GroupResult> groups = roll.performRoll(groupCount, random, getStatistics());
				String behaviors = Behavior.getPrettyString(roll);
				return buildString(getGroupCountString(groupCount) + roll.getRolled() + "k" + roll.getKept() + roll.getModifier() + behaviors, user, groups);
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
			return "";
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
		
		/**
		 * Builds the result string for the roll groups.
		 * @param groups The groups to use to build the string.
		 * @return the output string.
		 */
		private String buildString(String baseRoll, String user, List<GroupResult> groups) {
			if (groups.size() > 1) {
				StringBuilder dice = new StringBuilder();
				for (GroupResult group : groups) {
					dice.append(group.getDice());
					dice.append(" ");
				}
				
				StringBuilder modified = new StringBuilder();
				for (GroupResult group : groups) {
					modified.append(group.getModified());
					modified.append(" ");
				}
				return getPersonality().getRollResult("L5RMoreGroups", baseRoll, user, dice, modified);
			} else {
				GroupResult group = groups.get(0);
				return getPersonality().getRollResult("L5ROneGroup", baseRoll, user, group.getDice(), group.getModified());
			}
		}
	}
	
	/** A roller for handling White Wolf die behavior, like "7k3" or "2k1+2". */
	public static class WhiteWolfRoller extends Roller {
		/**
		 * Constructor.
		 * @param statistics The statistics object for tracking statistics.
		 * @param personality The object containing the dicebot personality.
		 */
		public WhiteWolfRoller(Statistics statistics, Personality personality) {
			super("^(\\d+)t(\\d+)(\\+\\d+|\\-\\d+)?(e)?([ ]*[dc|DC]+(\\d+))?", "White Wolf", getDesc(), 
					statistics, personality);
		}
		
		@Override
		public String assembleRoll(String[] parts, String user) throws InputException {
			short rolled = Utils.parseShort(parts[1], getPersonality());
			short neededSuccesses = Utils.parseShort(parts[2], getPersonality());
			Modifier modifier = Modifier.createModifier(parts[3], getPersonality());
			String emphasis = parts[4];
			String dcString = parts[5];
			Short dc = 6;
			if (null != dcString) {
				dc = Short.parseShort(parts[6]);
			}
			
			if (rolled < 1) {
				throw getPersonality().getException("Roll0Dice");
			} else if (dc < 0) {
				throw getPersonality().getException("DCLessThan0");
			} else if (rolled < neededSuccesses) {
				throw getPersonality().getException("CannotSatisfySuccesses", neededSuccesses, rolled);
			}
			
			Roll roll = new Roll(rolled, rolled, new SimpleDie((short) 10), modifier, null, null, getPersonality());
			List<GroupResult> groups = roll.performRoll(1, random, getStatistics());
			String behaviors = null == emphasis | "".equals(emphasis) ? "" : emphasis;
			dcString = null == dcString || "".equals(dcString) ? "" : " " + dcString; 
			return buildString(roll.getRolled() + "t" + neededSuccesses + modifier + behaviors + dcString, user, neededSuccesses, dc, modifier, 
					emphasis, groups.get(0));
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
		
		/**
		 * Builds the result string for the roll groups.
		 * @param baseRoll The base roll string.
		 * @param user The user who made the roll.
		 * @param neededSuccesses The number of successes needed for the roll.
		 * @param group The group to use to build the string.
		 * @return the output string.
		 */
		private String buildString(String baseRoll, String user, short neededSuccesses, short dc, Modifier modifier, 
				String specialization, GroupResult group) {
			long successesOverMinimum = -neededSuccesses;
			int onesRolled = 0;
			for (DieResult die : group.getDice()) {
			
				if (10 == die.getResult() && null != specialization) {
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
			
			if (successesOverMinimum >= 0) {
				return getPersonality().getRollResult("WhiteWolfSuccess", baseRoll, user, group.getDice(), successesOverMinimum, onesRolled);
			} else {
				return getPersonality().getRollResult("WhiteWolfFailure", baseRoll, user, group.getDice());
			}
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
		 * @param statistics The statistics object for tracking statistics.
		 * @param personality The object containing the dicebot personality.
		 */
		public FudgeRoller(Statistics statistics, Personality personality) {
			super("^(\\d+ )?(\\d+)d[fF](\\+\\d+|-\\d+)?", "Fudge",
					"A dice roller for the FUDGE dice style, which rolls X number of d6s with faces of ['-', '-', ' ', ' ', '+', '+'] and returns the additive result (ex. 4dF).", 
					statistics, personality);
		}

		@Override
		protected String assembleRoll(String[] parts, String user) throws InputException {
			short groupCount = parseGroups(parts[1]);
			short rolled = Utils.parseShort(parts[2], getPersonality());
			Modifier modifier = Modifier.createModifier(parts[3], getPersonality());
			
			if (rolled < 1) {
				throw getPersonality().getException("Roll0Dice");
			}
			
			Roll roll = new Roll(rolled, rolled, new FudgeDie(), modifier, null, null, getPersonality());
			List<GroupResult> groups = roll.performRoll(groupCount, random, getStatistics());
			return buildString(getGroupCountString(groupCount) + roll.getRolled() + "dF" + modifier, user, groups);
		}

		/**
		 * Builds the result string for the roll groups.
		 * @param groups The groups to use to build the string.
		 * @return the output string.
		 */
		private String buildString(String baseRoll, String user, List<GroupResult> groups) {
			if (groups.size() > 1) {
				List<Long> naturals = new ArrayList<Long>();
				List<Long> totals = new ArrayList<Long>();
				for (GroupResult group : groups) {
					naturals.add(group.getNatural());
					totals.add(group.getModified());
				}
				
				return getPersonality().getRollResult("FudgeMoreGroups", baseRoll, user, naturals, totals);
			} else {
				GroupResult group = groups.get(0);
				long total = group.getNatural();
				long modified = group.getModified();
				String descriptor = descriptors.get(modified);
				if (null == descriptor) {
					if (modified > 0) {
						descriptor = "Off The Scale!";
					} else {
						descriptor = "Did You Have Breakfast Today?";
					}
				}
				List<String> convDice = convertToFudgeDie(group.getDice());
				return getPersonality().getRollResult("Fudge1Group", baseRoll, user, convDice, total, modified, descriptor);
			}
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