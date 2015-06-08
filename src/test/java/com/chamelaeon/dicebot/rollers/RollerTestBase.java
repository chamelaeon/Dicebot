package com.chamelaeon.dicebot.rollers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.chamelaeon.dicebot.api.InputException;
import com.chamelaeon.dicebot.commands.CommandTestBase;

/**
 * Place for utility test code shared between rollers to live. 
 * @author Chamelaeon
 */
public class RollerTestBase extends CommandTestBase {

	public void doPersonalitySetup() throws InputException {
		super.doMockSetup();
		when(personality.parseShort(anyString())).thenAnswer(new ParsingAnswer());
		when(personality.parseDiceCount(anyString())).thenAnswer(new CheckingParsingAnswer());
	}
	
	public void doPersonalityTeardown() {
		verify(personality, atMost(50)).useCritFailures();
		verify(personality, atMost(50)).useCritSuccesses();
		super.doMockTeardown();
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
}