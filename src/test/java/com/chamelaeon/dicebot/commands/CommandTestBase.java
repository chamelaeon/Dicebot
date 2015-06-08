package com.chamelaeon.dicebot.commands;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.apache.commons.lang3.ObjectUtils;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.TokenSubstitution;

public class CommandTestBase {
    protected String testNick = "testNick";
    protected Personality personality;

    protected void doPersonalitySetup() throws InputException {
        personality = mock(Personality.class);
    }
    
    protected void doPersonalityTeardown() {
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