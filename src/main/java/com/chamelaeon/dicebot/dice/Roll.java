package com.chamelaeon.dicebot.dice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.dice.behavior.Explosion;
import com.chamelaeon.dicebot.dice.behavior.Reroll;
import com.chamelaeon.dicebot.random.Random;


/** Class that represents a roll of dice. */
public class Roll {
	/** The number of rolled dice. */
	private final short rolled;
	/** The number of kept dice . */
	private final short kept;
	/** The die type to be rolled. */
	private final Die die;
	/** The modifier for the roll. */
	private final Modifier modifier;
	/** The reroll criteria, if any. */
	private final Reroll reroll;
	/** The explosion criteria, if any. */
	private final Explosion explosion;
	/** The Personality object for providing exception texts. */
	private Personality personality;
	
	/**
	 * Constructs a new roll.
	 * @param rolled The number of rolled dice.
	 * @param kept The number of kept dice.
	 * @param sides The number of sides on the dice to be rolled.
	 * @param modifier The numerical modifier for the roll.
	 * @param reroll The reroll behavior for the roll, if any.
	 * @param explosion The explosion behavior for the roll, if any.
	 * @throws InputException if there is an issue with the construction of the roll.
	 */
	public Roll(short rolled, short kept, Die die, Modifier modifier, Reroll reroll, Explosion explosion, 
	        Personality personality) throws InputException {
		this.rolled = rolled;
		this.kept = kept;
		this.die = die;
		this.modifier = modifier;
		this.explosion = explosion;
		this.reroll = reroll;
		this.personality = personality;
		
		if (reroll != null && reroll.cannotBeSatisfied(die.getSides())) {
			if (rolled == 1) {
				throw personality.getException("CannotSatisfyRerollSingleDie", new TokenSubstitution("%REROLL%", reroll), 
				        new TokenSubstitution("%SIDES%", die.getSides()));
			} else {
				throw personality.getException("CannotSatisfyRerollMultipleDice", new TokenSubstitution("%REROLL%", reroll), 
				        new TokenSubstitution("%DICEROLLED%", rolled), new TokenSubstitution("%SIDES%", die.getSides()));
			}
		}
		
		if (explosion != null && explosion.explodesInfinitely(1)) {
			throw personality.getException("InfiniteExplosion");
		}
	}
	
	@Override
	public String toString() {
		return "Roll [rolled=" + rolled + ", kept=" + kept + ", die=" + die
				+ ", "
				+ (modifier != null ? "modifier=" + modifier + ", " : "")
				+ (reroll != null ? "reroll=" + reroll + ", " : "")
				+ (explosion != null ? "explosion=" + explosion + ", " : "")
				+ (personality != null ? "personality=" + personality : "")
				+ "]";
	}

	/**
	 * Makes a copy of the roll, with a few values altered, and returns it. Generally used for handling 
	 * dice "rollover" cases (like L5R).  
	 * @return the copied and altered roll.
	 * @throws InputException if the new roll cannot be constructed correctly.
	 */
	public Roll alterValues(int rolled, int kept, Modifier modifier) throws InputException {
		return alterValues((short) rolled, (short) kept, modifier);
	}
	
	/**
	 * Makes a copy of the roll, with a few values altered, and returns it. Generally used for handling 
	 * dice "rollover" cases (like L5R).  
	 * @return the copied and altered roll.
	 * @throws InputException if the new roll cannot be constructed correctly. 
	 */
	public Roll alterValues(short rolled, short kept, Modifier modifier) throws InputException {
		return new Roll(rolled, kept, this.die, modifier, this.reroll, this.explosion, this.personality);
	}
	
	/**
	 * Gets the number of rolled dice.
	 * @return the rolled count.
	 */
	public short getRolled() {
		return rolled;
	}
	
	/**
	 * Gets the number of kept dice.
	 * @return the kept count.
	 */
	public short getKept() {
		return kept;
	}

	/**
	 * Gets the die being used for the roll.
	 * @return the die.
	 */
	public Die getDie() {
		return die;
	}

	/**
	 * Gets the modifier for the roll.
	 * @return the modifier.
	 */
	public Modifier getModifier() {
		return modifier;
	}
	
	/** 
	 * Gets the reroll object for this roll, if any.
	 * @return the reroll.
	 */
	public Reroll getReroll() {
		return reroll;
	}
	
	/** 
	 * Gets the explosion object for this roll, if any.
	 * @return the explosion.
	 */
	public Explosion getExplosion() {
		return explosion;
	}

	/**
	 * Performs the actual roll.
	 * @param groupCount The number of groups to roll.
	 * @param random The random object for the roll.
	 * @param statistics The statistics tracking object.
	 * @return the result of all groups in the roll.
	 */
    public List<GroupResult> performRoll(int groupCount, Random random, Statistics statistics) {
		List<GroupResult> groups = new ArrayList<GroupResult>();
		statistics.addToGroups(groupCount);
		for (int i = 0; i < groupCount; i++) {
			// Generate the rolled dice.
			List<DieResult> dice = die.rollDice(rolled, random, reroll, explosion, statistics);
			Collections.sort(dice);
			Collections.reverse(dice);
			
			long natural = sumFirst(dice, kept);
			statistics.registerRoll(rolled + "-" + kept, natural);
			long modified = modifier.apply(natural);
			// Check for criticals.
			if ((natural == rolled) && personality.useCritFailures()) {
				groups.add(new GroupResult(dice, natural, modified, true, false));
			} else if (die.isCritSuccess(rolled, natural) && personality.useCritSuccesses()) {
				groups.add(new GroupResult(dice, natural, modified, false, true));
			} else {
				groups.add(new GroupResult(dice, natural, modified, false, false));
			}
		}
		
		return groups;
	}
	
	/**
     * Sums the first <code>count</code> values of the given list.
     *  
     * @param list The list to sum.
     * @param count The first X items to sum.
     * @return the sum.
     */
    private static long sumFirst(List<DieResult> list, int count) {
        long total = 0;
        for (int i = 0; i < count; i++) {
            total += list.get(i).getResult();
        }
        return total;
    }
}