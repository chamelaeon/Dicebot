package com.chamelaeon.dicebot.commands;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.ObjectUtils;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.pircbotx.Channel;
import org.pircbotx.output.OutputChannel;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.TokenSubstitution;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;

public class CommandTestBase {
    protected String testNick = "testNick";
    protected Personality personality;
    protected DicebotGenericEvent<Dicebot> event;
    protected Channel channel;
    protected OutputChannel outputChannel;
    protected Dicebot bot;

    @SuppressWarnings("unchecked")
    protected void doMockSetup() throws InputException {
        personality = mock(Personality.class);
        event = mock(DicebotGenericEvent.class);
        channel = mock(Channel.class);
        outputChannel = mock(OutputChannel.class);
        bot = mock(Dicebot.class);
        
        when(event.getChannel()).thenReturn(channel);
        when(event.getBot()).thenReturn(bot);
        when(bot.getPersonality()).thenReturn(personality);
        when(channel.send()).thenReturn(outputChannel);
    }
    
    protected void doMockTeardown() {
        verifyNoMoreInteractions(personality);
    }
    
    protected static TokenSubstitution tokenSubMatcher(String token, Object substitution) {
        return argThat(new TokenSubstitutionMatcher(token, substitution));
    }

    protected static class TokenSubstitutionMatcher extends ArgumentMatcher<TokenSubstitution> {
        private String token;
        private Object substitution;
        
        public TokenSubstitutionMatcher(String token, Object substitution) {
            this.token = token;
            this.substitution = substitution;
        }
        
        @Override
        public boolean matches(Object argument) {
            TokenSubstitution tokenSub = (TokenSubstitution) argument;
            
            if ("*".equals(substitution)) {
                return this.token.equals(tokenSub.getToken()); // TODO: Better condition for this.
            } else {
                return this.token.equals(tokenSub.getToken()) && ObjectUtils.equals(this.substitution, tokenSub.getSubstitution());
            }
        }
    
        @Override
        public void describeTo(Description description) {
            description.appendText("token='" + token + "' substitution='" + substitution + "'");
        }
    }
}