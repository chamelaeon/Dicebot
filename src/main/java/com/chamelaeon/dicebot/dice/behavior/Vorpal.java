package com.chamelaeon.dicebot.dice.behavior;

/** 
 * D&D 4e Vorpal explosion behavior.
 * @author Chamelaeon 
 */
public class Vorpal extends AbstractExplosion {
	/** 
	 * Private constructor.
	 * @param threshold The threshold.
	 */
	private Vorpal(Integer threshold) { 
	    super(threshold); 
	}
	
	@Override
	public String toString() { 
	    return "v" + getThreshold(); 
	}
	
	/**
     * Creates a factory for making Vorpal behaviors.
     * @return the factory.
     */
    public static BehaviorFactory getFactory() {
        return new BehaviorFactory() {
            @Override
            public Behavior createBehavior(int threshold) {
                return new Vorpal(threshold);
            }
        };
    }
}