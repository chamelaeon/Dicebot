package com.chamelaeon.dicebot.random;

import com.chamelaeon.dicebot.Statistics;

/**
 * Generates random numbers, for simulating dice rolling.
 * @author Chamelaeon
 */
public interface Random {

	/**
	 * Returns the next roll from the random number generator, in the range [1-diceType].
	 * @param diceType The type of dice to roll (e.g. d6).
	 * @return the result of the roll.
	 */
	public int getRoll(int diceType);

	/**
	 * Gets the {@link Statistics} object for this random number generator.
	 * @return the statistics object.
	 */
	public Statistics getStatistics();
	
	public static class BasicRandom implements Random {
		/** The statistics object for the roller. */
		private final Statistics statistics;
		/** The Random object for the roller. */
		private final java.util.Random random;
		
		/** 
		 * Creates a new {@link BasicRandom} with the given stats object.
		 * @param statistics The statistics object.	
		 */
		public BasicRandom(Statistics statistics) {
			this.statistics = statistics;
			this.random = new java.util.Random();
		}
		
		@Override
		public int getRoll(int diceType) {
			int val = random.nextInt(diceType) + 1;
			statistics.registerRoll(diceType, val);
			return val;
		}

		@Override
		public Statistics getStatistics() {
			return statistics;
		}
	}
	
	public static class MersenneTwisterRandom implements Random {
		/** The statistics object for the roller. */
		private final Statistics statistics;
		/** A length 624 array to store the state of the generator. */
		private int[] MT = new int[624];
		/** The index value. */
		private int index = 0;
		
		/** 
		 * Creates a new {@link MersenneTwisterRandom} with the given stats object.
		 * @param statistics The statistics object.	
		 */
		public MersenneTwisterRandom(Statistics statistics) {
			this.statistics = statistics;
			initializeGenerator((int) System.currentTimeMillis());
		}
		
		@Override
		public int getRoll(int diceType) {
			// Cribbed from java.util.Random itself.
			if (diceType <= 0) {
	            throw new IllegalArgumentException("n must be positive");
			}

//	        if ((diceType & -diceType) == diceType) { // i.e., n is a power of 2
//	            return (int)((diceType * (long) nextInt()) >> 31);
//	        }
	        
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
		
		@Override
		public Statistics getStatistics() {
			return statistics;
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
