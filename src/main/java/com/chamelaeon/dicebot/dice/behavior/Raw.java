/**
 * 
 */
package com.chamelaeon.dicebot.dice.behavior;

/**
 * Represents a roll which will never explode.
 * @author Chamelaeon
 */
public class Raw extends AbstractExplosion {
	
	/** Private constructor. */
    private Raw() {
		super(Integer.MAX_VALUE);
	}

	@Override
    public String toString() { 
        return "r"; 
    }

    /**
     * Creates a factory for making Raw behaviors.
     * @return the factory.
     */
    public static BehaviorFactory getFactory() {
        return new BehaviorFactory() {
            @Override
            public Behavior createBehavior(int threshold) {
                return new Raw();
            }
        };
    }
}
