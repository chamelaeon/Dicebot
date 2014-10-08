/**
 * 
 */
package com.chamelaeon.dicebot.statistics;

import org.apache.commons.math3.stat.inference.ChiSquareTest;


/**
 * Statistics implementation that can perform a chi-squared test.
 * @author Chamelaeon
 *
 */
public class ChiSquaredStatistics extends StandardStatistics {

	private long[] actual;
	
	public ChiSquaredStatistics() {
		actual = new long[0];
	}
	
	@Override
	public void registerRoll(int diceType, int rollValue) {
		super.registerRoll(diceType, rollValue);
		
		if (rollValue > actual.length) {
			long[] newArray = new long[rollValue];
			System.arraycopy(actual, 0, newArray, 0, actual.length);
			actual = newArray;
		}
		
		int foo = rollValue - 1;
		actual[foo] += 1;
	}

	public boolean performChiSquared(double[] expected) {
		ChiSquareTest chiSquared = new ChiSquareTest();
		
		System.out.println(chiSquared.chiSquareTest(expected, actual));
		return chiSquared.chiSquareTest(expected, actual, 0.05);
	}
}
