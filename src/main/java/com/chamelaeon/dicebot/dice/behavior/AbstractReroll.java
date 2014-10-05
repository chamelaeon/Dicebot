package com.chamelaeon.dicebot.dice.behavior;

/** 
 * Abstract class for handling most of the reroll framework.
 * @author Chamelaeon
 */
abstract class AbstractReroll extends Behavior implements Reroll {
    /** Protected constructor for children. */
    protected AbstractReroll(int range) {
        super(range);
    }

    @Override
    public boolean needsRerolled(int natural) { 
        return natural <= getThreshold(); 
    }

    @Override
    public boolean cannotBeSatisfied(int maxValue) { 
        return maxValue <= getThreshold(); 
    }
}