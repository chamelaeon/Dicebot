/**
 * 
 */
package com.chamelaeon.dicebot.dice;

import java.util.HashMap;
import java.util.Map;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;

/**
 * Represents a dice behavior. This can be something like "Explodes on 10s" or
 * "reroll on ones". 
 *  
 * @author Chamelaeon
 */
public abstract class Behavior {
	/** The map of flags to behaviors. */
	private static final Map<Character, Class<? extends Reroll>> REROLL_BEHAVIORS = new HashMap<Character, Class<? extends Reroll>>();
	private static final Map<Character, Class<? extends Explosion>> EXPLOSION_BEHAVIORS = new HashMap<Character, Class<? extends Explosion>>();
	static {
		REROLL_BEHAVIORS.put('e', Emphasis.class);
		REROLL_BEHAVIORS.put('b', Brutal.class);
		
		EXPLOSION_BEHAVIORS.put('m', Mastery.class);
		EXPLOSION_BEHAVIORS.put('v', Vorpal.class);
	}
	
	/**
	 * Parses the FIRST {@link Reroll} from the behavior string. Any
	 * others will be ignored.
	 * @param behaviorString The behavior string to parse.
	 * @param personality The personality to handle exceptions.
	 * @return the parsed reroll.
	 * @throws InputException if there is an issue with the configuration of the behaviors
	 */
	public static Reroll parseReroll(String behaviorString, Personality personality) throws InputException {
		if (null != behaviorString) {
			try {
				char[] characters = behaviorString.toCharArray();
				for (int i = 0; i < characters.length; i++) { 
					if (REROLL_BEHAVIORS.containsKey(characters[i])) {
						Class<? extends Reroll> clazz = REROLL_BEHAVIORS.get(characters[i]);
						Reroll reroll = clazz.newInstance();
						if (i + 1 != characters.length) {
							// If the next character is a digit, set the value.
							char nextChar = characters[i+1];
							if (Character.isDigit(nextChar)) {
								reroll.setThreshold(Character.getNumericValue(nextChar));
							}
						}
						return reroll;
					}
				}
			} catch (InstantiationException ie) {
				throw personality.getException("ReflectionError");
			} catch (IllegalAccessException ie) {
				throw personality.getException("ReflectionError");
			}
		}
		return null;
	}
	
	/**
	 * Parses the set of behaviors from the behavior string.
	 * @param behaviorString The behavior string to parse.
	 * @param personality The personality to handle exceptions.
	 * @return the parsed behavior set.
	 * @throws InputException if there is an issue with the configuration of the behaviors.
	 */
	public static Explosion parseExplosion(String behaviorString, Personality personality) throws InputException {
		return parseExplosion(behaviorString, 100, personality);
	}
	
	/**
	 * Parses the set of behaviors from the behavior string.
	 * @param behaviorString The behavior string to parse.
	 * @param maxDieValue The maximum value of a die in the given roll.
	 * @param personality The personality to handle exceptions.
	 * @return the parsed behavior set.
	 * @throws InputException if there is an issue with the configuration of the behaviors.
	 */
	public static Explosion parseExplosion(String behaviorString, int maxDieValue, Personality personality) throws InputException {
		if (null != behaviorString) {
			try {
				char[] characters = behaviorString.toCharArray();
				for (int i = 0; i < characters.length; i++) { 
					if (EXPLOSION_BEHAVIORS.containsKey(characters[i])) {
						Class<? extends Explosion> clazz = EXPLOSION_BEHAVIORS.get(characters[i]);
						Explosion explosion = clazz.newInstance();
						if (i + 2 < characters.length) {
							// Possible two-digit number.
							char nextChar = characters[i+1];
							char nextNextChar = characters[i+2];
							if (Character.isDigit(nextChar) && Character.isDigit(nextNextChar)) {
								String val = new String(new char[] {nextChar, nextNextChar});
								explosion.setThreshold(Integer.parseInt(val));
							} else if (Character.isDigit(nextChar)) {
								explosion.setThreshold(Character.getNumericValue(nextChar));
							}
						} else if (i + 1 < characters.length) {
							// Possible one-digit number.
							char nextChar = characters[i+1];
							if (Character.isDigit(nextChar)) {
								explosion.setThreshold(Character.getNumericValue(nextChar));
							}
						} else if (null == explosion.getRange()) {
							// If nothing else has set the range...
							//XXX: This is pretty much exclusively for vorpal. Wonder if there's a better way to handle this.
							explosion.setThreshold(maxDieValue);
						}
						return explosion;
					}
				}
			} catch (InstantiationException ie) {
				throw personality.getException("ReflectionError");
			} catch (IllegalAccessException ie) {
				throw personality.getException("ReflectionError");
			}
		}
		return null;
	}

	/**
	 * Gets a pretty string of all valid rerolls and explosions for the given roll.
	 * @param roll The roll to get the string for.
	 * @return the pretty string.
	 */
	public static String getPrettyString(Roll roll) {
		StringBuilder builder = new StringBuilder();
		if (null != roll.getReroll()) {
			builder.append(roll.getReroll());
		}
		if (null != roll.getExplosion()) {
			builder.append(roll.getExplosion());
		}
		return builder.toString();
	}
	
	/**
	 * Describes a behavior which causes reroll. 
	 * @author Chamelaeon
	 */
	public interface Reroll {
		/**
		 * Checks to see if a value needs rerolled.
		 * @param natural The value to check for rerolling.
		 * @return true if the value needs rerolled, false otherwise.
		 */
		public boolean needsRerolled(int natural);

		/**
		 * Whether the reroll should continue until there is a value which does not
		 * pass the {@link #needsRerolled(int)} check.
		 * @return true if the reroll should continue, false otherwise.
		 */
		public boolean forceGoodValue();
		
		/**
		 * Sets the threshold for the reroll. Anything below this will be rerolled.
		 * @param threshold The threshold value.
		 */
		public void setThreshold(int threshold);
		
		/**
		 * Checks to see if the given reroll condition cannot be satisfied (i.e. will always reroll).
		 * @param maxValue The maximum value of the dice.
		 * @return true if the reroll cannot be satisfied, false otherwise.
		 */
		public boolean cannotBeSatisfied(int maxValue);
	}
	
	/**
	 * Describes a behavior that causes explosions.
	 * @author Chamelaeon
	 */
	public interface Explosion {
		/**
		 * Checks to see if the value should explode.
		 * @param natural The value to check for explosion.
		 * @return true if the value should explode, false otherwise.
		 */
		public boolean shouldExplode(int natural);
		
		/**
		 * Sets the threshold for the explosion. Anything above this will be exploded.
		 * @param threshold The threshold value.
		 */
		public void setThreshold(int threshold);
		
		/**
		 * Checks to see if the given explosion will explode infinitely for the given minimum dice value.
		 * @param minValue The minimum dice value.
		 * @return true if the explosion will always happen, false otherwise.
		 */
		public boolean explodesInfinitely(int minValue);
		
		/** Gets the range. For private use only. */
		Integer getRange();
	}
	
	/** Abstract class for handling most of the reroll framework. */
	private static abstract class AbstractReroll implements Reroll {
		/** The range for the reroll. */
		protected int range; 
		/** Protected constructor for children. */
		protected AbstractReroll(int range) {
			this.range = range;
		}
		@Override
		public boolean needsRerolled(int natural) { return natural <= range; }
		@Override
		public boolean cannotBeSatisfied(int maxValue) { return maxValue <= range; }
		@Override
		public void setThreshold(int threshold) { range = threshold; }
	}
	
	/** Abstract class for handling most of the explosion framework. */
	private static abstract class AbstractExplosion implements Explosion {
		/** The range for the explosion. */
		protected Integer range; 
		/** Protected constructor for children. */
		protected AbstractExplosion(Integer range) {
			this.range = range;
		}
		@Override
		public boolean shouldExplode(int natural) { return natural >= range; }
		@Override
		public boolean explodesInfinitely(int minValue) { return minValue >= range; }
		@Override
		public void setThreshold(int threshold) { range = threshold; }
		@Override
		public Integer getRange() { return range; }
	}
	
	/** L5R Emphasis behavior. */
	public static class Emphasis extends AbstractReroll {
		/** Public constructor. */
		public Emphasis() { super(1); }
		@Override
		public boolean forceGoodValue() { return false; }
		@Override
		public String toString() { return "e"; }
		@Override
		public void setThreshold(int threshold) { /* Ignore. Emphasis threshold is always 1. */}
	}
	
	public static class Brutal extends AbstractReroll {
		/** Public constructor. */
		public Brutal() { super(0); }
		@Override
		public boolean forceGoodValue() { return true; }
		@Override
		public String toString() { return "b" + range; }
	}
	
	/** L5R Mastery behavior. */
	public static class Mastery extends AbstractExplosion {
		/** Public constructor. */
		public Mastery() { super(9); }
		@Override
		public String toString() { return "m"; }
		@Override
		public void setThreshold(int threshold) { /* Ignored. Mastery threshold is always 9. */ }
	}
	
	/** Default L5R explosion behavior. */
	public static class L5RExplosion extends AbstractExplosion {
		/** Public constructor. */
		public L5RExplosion() { super(10); }
		@Override
		public String toString() { return ""; }
		@Override
		public void setThreshold(int threshold) { /* Ignored. L5R threshold is always 10. */ }
	}
	
	/** D&D 4e Vorpal behavior. */
	public static class Vorpal extends AbstractExplosion {
		/** Public constructor. */
		public Vorpal() { super((Integer) null); }
		@Override
		public String toString() { return "v" + range; }
	}
}
