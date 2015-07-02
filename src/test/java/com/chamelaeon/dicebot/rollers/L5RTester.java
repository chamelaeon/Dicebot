package com.chamelaeon.dicebot.rollers;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.personality.BasicPersonality;
import com.chamelaeon.dicebot.statistics.StandardStatistics;
import com.google.common.io.Closeables;

/**
 * Driver program for testing L5R rolls and analyzing their statistics.
 * @author Chamelaeon
 */
public class L5RTester {

	/**
	 * Main.
	 * @param args The arguments.
	 */
	public static void main(String[] args) throws Exception {
	    Personality personality = new BasicPersonality();
	    
		final StandardStatistics statistics = new StandardStatistics();
		final AtomicBoolean running = new AtomicBoolean(true);
		final L5RRoller roller = new L5RRoller(personality);
		final int rolled = 10;
		final int kept = 10;
		final String behavior = "re";

		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while (running.get()) {
						String[] parts = new String[] {"", null, "" + rolled, "" + kept, behavior, null, null, null, null};
						roller.assembleRoll(parts, "", statistics);
					}
				} catch (InputException ie) {
					throw new RuntimeException(ie);
				}
			}
		});
		
		Scanner scanner= null;
		try {
			scanner = new Scanner(System.in);
			while(true) {
				String line = scanner.nextLine();
				
				if ("stop".equals(line)) {
					running.set(false);
					long diceCount = statistics.getDice();
					System.out.println("Rolled " + diceCount + " " + rolled + "k" + kept + " with behavior " + behavior + " for a mean average of " 
							+ statistics.getAverage(rolled + "-" + kept) + ".");
					System.exit(0);
				}
				if ("stats".equals(line)) {
					long diceCount = statistics.getDice();
					System.out.println("Rolled " + diceCount + " " + rolled + "k" + kept + " with behavior " + behavior + " for a mean average of " 
							+ statistics.getAverage(rolled + "-" + kept) + ".");
				}
			}
		} finally {
		    Closeables.close(scanner, false);
		}
	}

}
