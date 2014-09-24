package com.chamelaeon.dicebot.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

/**
 * A database of cards to draw from.
 * TODO: This should probably be an interface with an properties/xml implementation.
 * @author Chamelaeon
 */
public class CardBase {
	/** The cards. */
	private final Properties cards;
	/** The Random. */
	private final Random random;
	
	/**
	 * Constructor.
	 * @param stream The input stream to the XML of the cards.
	 */
	public CardBase(InputStream stream) {
		cards = new Properties();
		random = new Random();
		try {
			cards.load(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Draws a card.
	 * @return The drawn card.
	 */
	public String draw() {
		int randomIndex = random.nextInt(cards.size());
		Enumeration<Object> cardEnum = cards.elements();
		Enumeration<Object> nameEnum = cards.keys();
		int count = 0;
		while (cardEnum.hasMoreElements() && count++ < randomIndex) {
			cardEnum.nextElement();
			nameEnum.nextElement();
		}
		
		String title = ((String) nameEnum.nextElement()).replace('_', ' ');
		String card = title + " - " + (String) cardEnum.nextElement();
		return card;
	}
}

