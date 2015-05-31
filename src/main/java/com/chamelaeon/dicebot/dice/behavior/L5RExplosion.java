package com.chamelaeon.dicebot.dice.behavior;

/** 
 * Default L5R explosion behavior. 
 * @author Chamelaeon 
 */
public class L5RExplosion extends AbstractExplosion {
    /** Public constructor. */
    public L5RExplosion() { 
        super(10); 
    }

    @Override
    public String toString() { 
        // This happens on every roll, so it's expected default behavior and shouldn't be called out.
        return ""; 
    }
}