package com.chamelaeon.dicebot.random;

import com.chamelaeon.dicebot.dice.IStatistics;
import com.chamelaeon.dicebot.dice.NullStatistics;

/**
 * Generates random numbers, for simulating dice rolling.
 * @author Chamelaeon
 */
public interface Random {

    /**
     * Returns the next roll from the random number generator, in the range [1-diceType]. The roll is not tracked
     * by statistics.
     * @param diceType The type of dice to roll (e.g. d6).
     * @return the result of the roll.
     */
    public int getRoll(int diceType);
    
	/**
	 * Returns the next roll from the random number generator, in the range [1-diceType]. The roll is tracked
	 * in the given Statistics object.
	 * @param diceType The type of dice to roll (e.g. d6).
	 * @param statistics The statistics to use to track the roll.
	 * @return the result of the roll.
	 */
	public int getRoll(int diceType, IStatistics statistics);

	/** A Random implementation that just uses the base Java Random. */
	public static class BasicRandom implements Random {
		/** The Random object for the roller. */
		private final java.util.Random random;
		
		/** Creates a new {@link BasicRandom}. */
		public BasicRandom() {
			this.random = new java.util.Random();
		}
		
		@Override
        public int getRoll(int diceType) {
		    return getRoll(diceType, new NullStatistics());
        }

        @Override
		public int getRoll(int diceType, IStatistics statistics) {
			int val = random.nextInt(diceType) + 1;
			statistics.registerRoll(diceType, val);
			return val;
		}
	}
	
	/** A Random implementation that uses a Mersenne Twister as the PRNG. */
	public static class MersenneTwisterRandom implements Random {
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
		public int getRoll(int diceType, IStatistics statistics) {
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
		
		 // Initialize the generator from a seed
		 public void initializeGenerator(int seed) {
		     MT[0] = seed;
		     for (int i = 1; i < 624; i++) { // loop over each other element
		         MT[i] = (0x6c078965 * (MT[i-1] ^ (MT[i-1] >>> 30))) + i;
		     }
		 }
		 
		 // Generate an array of 624 untempered numbers
		 public void generateNumbers() {
			for (int i = 0; i < 624; i++) {
				int y = (MT[i] & 0x80000000) | (MT[(i + 1) % 624] & 0x7fffffff);
				MT[i] = MT[(i + 397) % 623] ^ (y >>> 1);
				if (y % 2 != 0) {
					MT[i] = MT[i] ^ 0x9908b0df;
				}
			}
		 }
	}	
}
