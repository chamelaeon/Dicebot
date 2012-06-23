package com.chamelaeon.dicebot.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


import com.chamelaeon.dicebot.Statistics;
import com.chamelaeon.dicebot.random.Random.MersenneTwisterRandom;

public class RandomD20Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Statistics statistics = new Statistics();
		Scanner scanner = new Scanner(System.in);
		final AtomicBoolean running = new AtomicBoolean(true);
		final MersenneTwisterRandom random = new MersenneTwisterRandom(statistics);
		final AtomicInteger doubles = new AtomicInteger(0);
		final Map<Integer, AtomicLong> cells = new HashMap<Integer, AtomicLong>();
		for (int i = 1; i < 21; i++) {
			cells.put(i, new AtomicLong());
		}
		
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				int lastRoll = 0;
				while (running.get()) {
					int thisRoll = random.getRoll(20);
					cells.get(thisRoll).incrementAndGet();
					if (thisRoll == lastRoll) {
						doubles.incrementAndGet();
					}
					lastRoll = thisRoll;
				}
			}
		});
		
		while(true) {
			String line = scanner.nextLine();
			
			if ("stop".equals(line)) {
				running.set(false);
				long diceCount = statistics.getDice();
				int dubs = doubles.get();
				System.out.println("Rolled " + diceCount + " d20s for an average roll of " + statistics.getAverage(20) + ".");
				System.out.println("Got " + dubs + " doubles, for a percentage of " + (doubles.get() / (1.0 * statistics.getDice())));
				System.out.print("Observed frequencies (1 through 20 in order):");
				for (Entry<Integer, AtomicLong> entry : cells.entrySet()) {
					double frequency = diceCount / entry.getValue().doubleValue();
					System.out.print(" " + frequency);
				}
				System.out.println(". Expected frequency: " + (1.0 / 20.0));
				System.exit(0);
			}
			if ("stats".equals(line)) {
				long diceCount = statistics.getDice();
				int dubs = doubles.get();
				System.out.println("Rolled " + diceCount + " d20s for an average roll of " + statistics.getAverage(20) + ".");
				System.out.println("Got " + dubs + " doubles, for a percentage of " + (dubs / (1.0 * diceCount)));
				System.out.print("Observed frequencies (1 through 20 in order):");
				for (Entry<Integer, AtomicLong> entry : cells.entrySet()) {
					double frequency = diceCount / entry.getValue().doubleValue();
					System.out.print(" " + frequency);
				}
				System.out.println(". Expected frequency: " + (1.0 / 20.0));
			}
		}
	}
}
