package com.chamelaeon.dicebot.commands;

import java.util.regex.Matcher;

import com.chamelaeon.dicebot.CardBase;
import com.chamelaeon.dicebot.Dicebot;

/** A command to draw a drama card. */
public class DrawCardCommand implements Command {

	/** The dicebot to use to send messages. */
	private final Dicebot dicebot;
	/** The database of cards to draw from. */
	private CardBase cardBase;
	
	/**
	 * Constructor.
	 * @param parent The parent dicebot of this command.
	 */
	public DrawCardCommand(Dicebot parent, CardBase cardBase) {
		this.dicebot = parent;
		this.cardBase = cardBase;
	}
	
	@Override
	public String execute(Matcher matcher, String source, String user) {
		if (matcher.groupCount() >= 1) {
			String countString = matcher.group(1).trim();
			String notifyNick = null;
			if (null != matcher.group(2)) {
				notifyNick = matcher.group(2).trim();
			}

			short count;
			try {
				count = Short.parseShort(countString);
			} catch (NumberFormatException nfe) {
				// Regexp should prevent this from happening, but...
				return "The number of cards has to be a number!";
			}
			
			// Hit up the card DB.
			for (int i = 0; i < count; i++) {
				// TODO: Add rarity
				String card = cardBase.draw();
				
				// Send a msg to the originating user, and the notified user.
				if (null != notifyNick) {
					dicebot.sendMessage(notifyNick, user + " drew a card: " + card);
				}
				dicebot.sendMessage(user, "You drew a card: " + card);
			}
			if (null != notifyNick) {
				return "draws " + count + " cards for " + user + " and notifies " + notifyNick;
			} else {
				return "draws " + count + " cards for " + user + ". It's a secret to everyone.";
			}
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "Draws a number of drama cards, from 1 to 5.";
	}

	@Override
	public String getRegexp() {
		return "draw ([1-5])( [a-zA-Z0-9-<\\[\\]\\{\\}]+)?" ;
	}
}