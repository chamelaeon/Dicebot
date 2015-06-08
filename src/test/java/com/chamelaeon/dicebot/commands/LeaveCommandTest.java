package com.chamelaeon.dicebot.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.HelpDetails;

public class LeaveCommandTest extends CommandTestBase {

    LeaveCommand command;
    
    @Before
    public void setUp() throws Exception {
        doMockSetup();
        command = new LeaveCommand();
    }
    
    @After
    public void tearDown() throws Exception {
        doMockTeardown();
    }

    @Test
    public void testLeave() {
        List<String> groups = new ArrayList<String>();
        String leaveMessage = "leave message";
        when(personality.getMessage("Leave")).thenReturn(leaveMessage);
        command.onSuccess(event, groups);
        
        verify(personality).getMessage("Leave");
        verify(outputChannel).part(leaveMessage);
    }
    
    @Test
    public void testLeaveWithCorrectName() {
        String botName = "RightName"; 
        List<String> groups = Arrays.asList("!join " + botName, botName);
        String leaveMessage = "leave message";
        
        when(personality.getMessage("Leave")).thenReturn(leaveMessage);
        when(bot.getNick()).thenReturn(botName);
        command.onSuccess(event, groups);
        
        verify(personality).getMessage("Leave");
        verify(outputChannel).part(leaveMessage);
    }
    
    @Test
    public void testLeaveWithCorrectLowercaseName() {
        String botName = "RightName"; 
        List<String> groups = Arrays.asList("!join " + botName.toLowerCase(), botName.toLowerCase());
        String leaveMessage = "leave message";
        
        when(personality.getMessage("Leave")).thenReturn(leaveMessage);
        when(bot.getNick()).thenReturn(botName);
        command.onSuccess(event, groups);
        
        verify(personality).getMessage("Leave");
        verify(outputChannel).part(leaveMessage);
    }
    
    @Test
    public void testLeaveWithIncorrectName() {
        String botName = "RightName"; 
        List<String> groups = Arrays.asList("!join wrongName", "wrongName");
        
        when(bot.getNick()).thenReturn(botName);
        command.onSuccess(event, groups);
        
        verifyZeroInteractions(outputChannel);
    }
    
    @Test
    public void testHelpDetails() {
        HelpDetails details = command.getHelpDetails();
        assertEquals("leave", details.getCommandName());
        assertEquals("Makes the bot leave the channel that this command is used in. If a nick is provided, only a bot with that nick will leave.", 
                details.getDescription());
        assertEquals("command", details.getType());

        List<String> list = new ArrayList<>();
        list.add("!leave");
        list.add("!leave botnick");
        assertEquals(list, details.getExamples());
    }
}
