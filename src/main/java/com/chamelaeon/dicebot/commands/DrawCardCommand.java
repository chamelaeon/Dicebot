package com.chamelaeon.dicebot.commands;

import java.util.List;

import org.pircbotx.User;

import com.chamelaeon.dicebot.CardBase;
import com.chamelaeon.dicebot.Dicebot;
import com.chamelaeon.dicebot.InputException;
import com.chamelaeon.dicebot.listener.DicebotGenericEvent;
import com.chamelaeon.dicebot.listener.DicebotListenerAdapter;


/** 
 * A command to draw a drama card.
 * @author Chamelaeon 
 */
public class DrawCardCommand extends DicebotListenerAdapter {

	/** The database of cards to draw from. */
	private CardBase cardBase;
	
	/**
	 * Constructor.
	 * @param cardBase The database of cards to draw from.
	 */
	public DrawCardCommand(CardBase cardBase) {
		super("!draw ([1-5])( [a-zA-Z0-9-<\\[\\]\\{\\}]+)?", 
				new HelpDetails("draw", "Draws a number of drama cards, from 1 to 5."));
		this.cardBase = cardBase;
	}
	
	@Override
	public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) throws InputException {
		if (groups.size() >= 1) {
			String countString = groups.get(1);
			String notifyNick = null;
			if (null != groups.get(2)) {
				notifyNick = groups.get(2);
			}

			short count;
			try {
				count = Short.parseShort(countString);
			} catch (NumberFormatException nfe) {
				// Regexp should prevent this from happening, but...
				throw new InputException("The number of cards has to be a number!");
			}
			
			User user = event.getUser();
			
			// Hit up the card DB.
			for (int i = 0; i < count; i++) {
				// TODO: Add rarity
				String card = cardBase.draw();
				
				// Send a msg to the originating user, and the notified user.
				if (null != notifyNick) {
					event.getBot().sendIRC().message(notifyNick, user.getNick() + " drew a card: " + card);
				}
				user.send().message("You drew a card: " + card);
			}

			if (null != notifyNick) {
				event.respondWithAction("draws " + count + " cards for " + user.getNick() + " and notifies " + notifyNick + ".");
			} else {
			    event.respondWithAction("draws " + count + " cards for " + user.getNick() + ". It's a secret to everyone.");
			}
		}
	}
}