package com.chamelaeon.dicebot.random;

import com.chamelaeon.dicebot.api.Statistics;
import com.chamelaeon.dicebot.statistics.NullStatistics;

/** 
 * A Random implementation that uses a Mersenne Twister as the PRNG.
 * @author Chamelaeon
 */
public class MersenneTwisterRandom implements Random {
    /** A length 624 array to store the state of the generator. */
    private int[] MT = new int[624];
    /** The index value. */
    private int index = 0;

    /**  Creates a new {@link MersenneTwisterRandom}. */
    public MersenneTwisterRandom() {
        initializeGenerator((int) System.currentTimeMillis());
    }

    @Override
    public int getRoll(int diceType) {
        return getRoll(diceType, new NullStatistics());
    }

    @Override
    public int getRoll(int diceType, Statistics statistics) {
        // Cribbed from java.util.Random itself.
        if (diceType <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }

        int bits = 0;
        int val = 0;
        int condition = 0;
        do {
            bits = (nextInt() >>> 1);
            val = bits % diceType;
            condition = bits - val + (diceType - 1);
        } while (condition < 0);

        if (val < 0) {
            System.out.println("Encountered negative result " + val + "!!");
            System.out.println(Integer.toBinaryString(bits));
            System.out.println(condition);
            System.exit(1);
        }

        val += 1;
        statistics.registerRoll(diceType, val);
        return val;
    }

    /**
     * Returns the next mersenne twister int.
     * @return the next int.
     */
    private int nextInt() {
        if (index == 0) {
            generateNumbers();
        }
        int y = MT[index];

        // Tempering!
        y ^= (y >>> 11);
        y ^= (y << 7) & (0x9d2c5680);
        y ^= (y << 15) & (0xefc60000); 
        y ^= (y >>> 18);

        index = (index + 1) % 624;
        return y;
    }

    /**
     * Initialize the generator from a seed
     * @param seed The seed to initialize with.
     */
    private void initializeGenerator(int seed) {
        MT[0] = seed;
        for (int i = 1; i < 624; i++) { // loop over each other element
            MT[i] = (0x6c078965 * (MT[i-1] ^ (MT[i-1] >>> 30))) + i;
        }
    }

    /** Generate an array of 624 untempered numbers. */
    private void generateNumbers() {
        for (int i = 0; i < 624; i++) {
            int y = (MT[i] & 0x80000000) | (MT[(i + 1) % 624] & 0x7fffffff);
            MT[i] = MT[(i + 397) % 623] ^ (y >>> 1);
            if (y % 2 != 0) {
                MT[i] = MT[i] ^ 0x9908b0df;
            }
        }
    }
}