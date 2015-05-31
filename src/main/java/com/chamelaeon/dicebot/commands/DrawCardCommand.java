package com.chamelaeon.dicebot.commands;

import java.util.Arrays;
import java.util.List;

import org.pircbotx.User;

import com.chamelaeon.dicebot.api.CardBase;
import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;


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
				new HelpDetails("draw", "Draws a number of drama cards, from 1 to 5. " 
				        + "The results are reported via private message. A second player can optionally be notified privately as well.",
				Arrays.asList("!draw PlayerOne", "!draw PlayerOne GameMaster")));
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
				throw new InputException(event.getBot().getPersonality().getMessage("DrawNonNumberCards"));
			}
			
			User user = event.getUser();
			
			// Hit up the card DB.
			for (int i = 0; i < count; i++) {
				String card = cardBase.draw();
				
				// Send a msg to the originating user, and the notified user.
				if (null != notifyNick) {
					event.getBot().sendIRC().message(notifyNick, user.getNick() + " drew a card: " + card);
				}
				user.send().message("You drew a card: " + card);
			}

			if (null != notifyNick) {
				event.respondWithAction(event.getBot().getPersonality().getMessage("DrawCardAndNotify", 
				        new TokenSubstitution("%CARDCOUNT%", count), new TokenSubstitution("%NICK%", user.getNick()), 
				        new TokenSubstitution("%NOTIFYNICK%", notifyNick)));
			} else {
			    event.respondWithAction(event.getBot().getPersonality().getMessage("DrawCard", 
			            new TokenSubstitution("%CARDCOUNT%", count), new TokenSubstitution("%NICK%", user.getNick())));
			}
		}
	}
}