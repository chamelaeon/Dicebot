package com.chamelaeon.dicebot.rollers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.ObjectUtils;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.api.Personality;
import com.chamelaeon.dicebot.api.TokenSubstitution;

/**
 * Place for utility test code shared between rollers to live. 
 * @author artdoler
 */
public class RollerTestBase {

	String testNick = "testNick";
	Personality personality;
	
	public void doPersonalitySetup() throws InputException {
		personality = mock(Personality.class);
		
		when(personality.parseShort(anyString())).thenAnswer(new ParsingAnswer());
		when(personality.parseDiceCount(anyString())).thenAnswer(new CheckingParsingAnswer());
	}
	
	public void doPersonalityTeardown() {
		verify(personality, atMost(50)).useCritFailures();
		verify(personality, atMost(50)).useCritSuccesses();
		verifyNoMoreInteractions(personality);
	}
	
	protected static class ParsingAnswer implements Answer<Short> {
	    @Override
	    public Short answer(InvocationOnMock invocation) throws Throwable {
	        Object[] args = invocation.getArguments();
	        return Short.parseShort(((String) args[0]).trim());
	    }
	}
	
	protected static class CheckingParsingAnswer implements Answer<Short> {
	    @Override
	    public Short answer(InvocationOnMock invocation) throws Throwable {
	        Object[] args = invocation.getArguments();
	        
	        if (null != args[0] && !((String) args[0]).isEmpty()) {
	        	return Short.parseShort(((String) args[0]).trim());
	        } else {
	            return 1;
	        }
	        
	    }
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