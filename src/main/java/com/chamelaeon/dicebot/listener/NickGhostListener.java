/**
 * 
 */
package com.chamelaeon.dicebot.listener;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;

import com.chamelaeon.dicebot.api.Dicebot;

/**
 * @author Chamelaeon
 *
 */
public class NickGhostListener extends ListenerAdapter<Dicebot> implements NickHandlingListener {
    /** The nickserv password. */
    private final String nickservPassword;
    private boolean haveGhosted;
    private boolean shouldGhost;
    private String usedNick;
    
    /**
     * Constructor.
     * @param nickservPassword The nickserv password.
     */
    public NickGhostListener(String nickservPassword) {
        this.nickservPassword = nickservPassword;
        haveGhosted = false;
        shouldGhost = false;
    }

    @Override
    public void onDisconnect(DisconnectEvent<Dicebot> event) throws Exception {
        haveGhosted = false;
    }

    @Override
    public void onNickAlreadyInUse(NickAlreadyInUseEvent<Dicebot> event) throws Exception {
        shouldGhost = true;
        usedNick = event.getUsedNick();
        event.respond(usedNick + "_");
    }
    
    @Override
    public void onConnect(ConnectEvent<Dicebot> event) throws Exception {
        if (shouldGhost && !haveGhosted) {
            System.out.println("Ghosting existing nick " + usedNick);
            event.getBot().sendRaw().rawLine("NICKSERV GHOST " + usedNick + " " + nickservPassword);
            event.getBot().sendIRC().changeNick(usedNick);
            haveGhosted = true;
            usedNick = "";
            shouldGhost = false;
        }
    }
    
    @Override
    public void resetNickIndex() {
        // Nothing to do.
    }
}
