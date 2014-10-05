/**
 * 
 */
package com.chamelaeon.dicebot.dice.behavior;

/**
 * @author Chamelaeon
 *
 */
public interface BehaviorFactory {

    /**
     * 
     * @param threshold
     * @return
     */
    public Behavior createBehavior(int threshold);
}
