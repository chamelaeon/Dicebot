package com.chamelaeon.dicebot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import com.chamelaeon.dicebot.personality.PropertiesPersonality;

/**
 * Driver class for the dicebot.
 * @author Chamelaeon
 */
public class DicebotRunner {

	/**
	 * Runs the dicebot.
	 * @param args The first arg should be a properties file containing the props for the dicebot.
	 */
	public static void main(String[] args) throws Exception {
		// Find the arguments.
		String fileName = args[0];
		String cardPath = args[1];
		// Grab the properties.
		Properties props = new Properties();
		InputStream propStream = null;
		try {
			propStream = new FileInputStream(new File(fileName));
			props.load(propStream);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try { propStream.close(); } catch (IOException ioe) { /* Ignore. */ }
		}
		
		String network = props.getProperty("Network", "irc.sandwich.net");
		String nicks = props.getProperty("Nicks", "Chibi_Vernon,Walrus_Vernon");
		String channels = props.getProperty("Channels", "#digbitching,#atc,#besm");
		boolean verbose = Boolean.parseBoolean(props.getProperty("Verbose", "false"));
		
		// Now start our bot up.
        Dicebot bot = new Dicebot(network, new PropertiesPersonality(props, cardPath), Arrays.asList(nicks.split(",")), Arrays.asList(channels.split(",")));
        bot.setVerbose(verbose);
        bot.start();

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\\n");
        while (scanner.hasNext()) {
        	String line = scanner.next();
        	
        	if (line.equals("exit") || line.equals("quit")) {
        		bot.disconnect();
        		bot.dispose();
        		System.exit(0);
        	}
        	String[] parts = line.split(" ");
        	String channel = parts[0];
        	
        	if (parts[1].equals("/me")) {
        		String message = line.substring(channel.length() + 4).trim();
        		bot.sendAction(channel, message);
        	} else {
        		String message = line.substring(channel.length()).trim();
        		bot.sendMessage(channel, message);
        	}
        }
        
	}

}
