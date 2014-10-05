package com.chamelaeon.dicebot.dice.behavior;

/** 
 * L5R Emphasis reroll behavior. 
 * @author Chamelaeon
 */
public class Emphasis extends AbstractReroll {
    /** Private constructor. */
    private Emphasis() { 
        super(1); 
    }

    @Override
    public boolean forceGoodValue() { 
        return false; 
    }

    @Override
    public String toString() { 
        return "e"; 
    }

    /**
     * Creates a factory for making Emphasis behaviors.
     * @return the factory.
     */
    public static BehaviorFactory getFactory() {
        return new BehaviorFactory() {
            @Override
            public Behavior createBehavior(int threshold) {
                return new Emphasis();
            }
        };
    }
}