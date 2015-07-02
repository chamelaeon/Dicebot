package com.chamelaeon.dicebot.dice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;


/** Class describing modifiers. */
public abstract class Modifier {
    /** The regex pattern for pulling out modifiers from the compound string. */
    private static final Pattern MODIFIER_PATTERN = Pattern.compile("(\\+\\d+|-\\d+)");
    
	/** The value of the modifier. */
	private int value;

	/**
	 * Protected constructor for child classes. 
	 * @param value The value of the modifier.
	 */
	protected Modifier(int value) {
		this.value = value;
	}
	
	/**
	 * Applies the modifier to the given roll result.
	 * @param rollResult The roll result to modify.
	 * @return the modified value.
	 */
	public long apply(long rollResult) {
		return rollResult + value;
	}  
	
	/**
     * Applies the modifier to the given roll result.
     * @param rollResult The roll result to modify.
     * @return the modified value.
     */
    public double apply(double rollResult) {
        return rollResult + value;
    }
	
	/**
	 * Adds a delta to the value of the modifier.
	 * @param delta The delta to add.
	 * @return the new altered modifier.
	 */
	public abstract Modifier appendToValue(int delta);
	
	/**
	 * Creates a Modifier from the given string.
	 * @param compundModifierString The compound modifier string to parse.
	 * @param personality The personality to handle the exception.
	 * @throws InputException if there is a problem parsing the modifier.
	 */
	public static Modifier createModifier(String compundModifierString, Personality personality) throws InputException {
		if (null != compundModifierString) {
		    Matcher matcher = MODIFIER_PATTERN.matcher(compundModifierString);
		    Short modifier = null;
		    while (matcher.find()) {
		        if (null == modifier) {
		            modifier = 0;
		        }
		        
		        String modifierString = matcher.group(0);
		        short value = personality.parseShort(modifierString.substring(1));
	            if (modifierString.startsWith("+")) {
	               modifier = (short) (modifier + value);
	            } else if (modifierString.startsWith("-")) {
	                modifier = (short) (modifier - value);
	            } else {
	                throw personality.getException("BrokenRegexp");
	            }    
		    }
		    
		    if (null == modifier) {
		        throw personality.getException("BrokenRegexp");
		    } else if (modifier >= 0) {
		        return new PositiveModifier(modifier);
		    } else {
		        return new NegativeModifier(-modifier);
		    }
		} else {
			return new PositiveModifier(0);
		}
	}
	
	@Override
	public String toString() {
		if (value > 0) {
			return "+" + value;
		} else if (value < 0) {
			return "" + value;
		} else {
			return "";
		}
	}
	
	/** A modifier which applies a positive value. */
	private static class PositiveModifier extends Modifier {
		/**
		 * Constructs a new {@link PositiveModifier} with the given value.
		 * @param value The value of the modifier.
		 */
		public PositiveModifier(int value) {
			super(value);
		}

		@Override
		public Modifier appendToValue(int delta) {
			return new PositiveModifier(super.value + delta);
		}			
	}
	
	/** A modifier which applies a negative value. */
	private static class NegativeModifier extends Modifier {
		/**
		 * Constructs a new {@link NegativeModifier} with the given value.
		 * @param value The value of the modifier.
		 */
		public NegativeModifier(int value) {
			super(-value);
		}
		
		@Override
		public Modifier appendToValue(int delta) {
			// Always use Positive here, because the value might be negative.
 			return new PositiveModifier(super.value + delta);
		}
	}
}