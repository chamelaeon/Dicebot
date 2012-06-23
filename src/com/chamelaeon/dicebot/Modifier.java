package com.chamelaeon.dicebot;

import com.chamelaeon.dicebot.personality.Personality;


/** Class describing modifiers. */
public abstract class Modifier {
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
	 * Adds a delta to the value of the modifier.
	 * @param delta The delta to add.
	 * @return the new altered modifier.
	 */
	public abstract Modifier appendToValue(int delta);
	
	/**
	 * Creates a Modifier from the given string.
	 * @param modifierString The modifier to parse.
	 * @param personality The personality to handle the exception.
	 * @throws InputException if there is a problem parsing the modifier.
	 */
	public static Modifier createModifier(String modifierString, Personality personality) throws InputException {
		if (null != modifierString) {
			short value = Utils.parseShort(modifierString.substring(1), personality);
			if (modifierString.startsWith("+")) {
				return new PositiveModifier(value);
			} else if (modifierString.startsWith("-")) {
				return new NegativeModifier(value);
			} else {
				throw personality.getException("BrokenRegexp");
			}
		} else {
			return new PositiveModifier(0);
		}
	}
	
	/**
	 * Creates a Modifier that does nothing.
	 * @return a modifier that does nothing.
	 */
	public static Modifier createNullModifier() {
		return new NullModifier();
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
	
	/** A modifier that does nothing. */
	private static class NullModifier extends Modifier {
		/** Constructs a new {@link NullModifier} */
		public NullModifier() {
			super(0);
		}
		
		@Override
		public Modifier appendToValue(int delta) {
			return this;
		}
	}
}