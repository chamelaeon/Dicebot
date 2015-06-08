package com.chamelaeon.dicebot.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.Channel;
import org.pircbotx.output.OutputChannel;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;

public class LeaveCommandTest extends CommandTestBase {

    LeaveCommand command;
    
    DicebotGenericEvent<Dicebot> event;
    Channel channel;
    OutputChannel outputChannel;
    Dicebot bot;
    
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        doPersonalitySetup();
        command = new LeaveCommand();
        event = mock(DicebotGenericEvent.class);
        channel = mock(Channel.class);
        outputChannel = mock(OutputChannel.class);
        bot = mock(Dicebot.class);
        
        when(event.getChannel()).thenReturn(channel);
        when(event.getBot()).thenReturn(bot);
        when(bot.getPersonality()).thenReturn(personality);
        when(channel.send()).thenReturn(outputChannel);
    }
    
    @After
    public void tearDown() throws Exception {
        doPersonalityTeardown();
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
