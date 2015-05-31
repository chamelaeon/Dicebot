package com.chamelaeon.dicebot.dice.behavior;

/** 
 * Abstract class for handling most of the explosion framework. 
 * @author Chamelaeon
 */
abstract class AbstractExplosion extends Behavior implements Explosion {
    /** Protected constructor for children. */
    protected AbstractExplosion(Integer range) {
       super(range);
    }

    @Override
    public boolean shouldExplode(int natural) { 
        return natural >= getThreshold(); 
    }

    @Override
    public boolean explodesInfinitely(int minValue) { 
        return minValue >= getThreshold(); 
    }
}