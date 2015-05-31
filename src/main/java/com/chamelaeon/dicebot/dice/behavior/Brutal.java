package com.chamelaeon.dicebot.dice.behavior;

/**
 * D&D 4e brutal reroll behavior.
 * @author Chamelaeon
 */
public class Brutal extends AbstractReroll {
	/**
	 * Private constructor.
	 * @param threshold The threshold for the brutal behavior.
	 */
	public Brutal(int threshold) { 
	    super(threshold); 
	}
	
	@Override
	public boolean forceGoodValue() { 
	    return true; 
	}
	
	@Override
	public String toString() { 
	    return "b" + getThreshold(); 
	}
	
	/**
     * Creates a factory for making Emphasis behaviors.
     * @return the factory.
     */
    public static BehaviorFactory getFactory() {
        return new BehaviorFactory() {
            @Override
            public Behavior createBehavior(int threshold) {
                return new Brutal(threshold);
            }
        };
    }
}