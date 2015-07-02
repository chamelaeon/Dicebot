package com.chamelaeon.dicebot.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.HelpDetails;

public class HelpCommandTest extends CommandTestBase {

    HelpCommand command;
    List<HelpDetails> commandHelpDetails;    
    List<HelpDetails> rollerHelpDetails;
    
    @Before
    public void setUp() throws Exception {
        doMockSetup();
        commandHelpDetails = new ArrayList<HelpDetails>();
        rollerHelpDetails = new ArrayList<HelpDetails>();
    }
    
    @After
    public void tearDown() throws Exception {
        doMockTeardown();
    }
    
    @Test
    public void testMessageWithNullGroup() {
        List<String> groups = Arrays.asList("!help", null);
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Here is a list of the commands I can perform: help");
        verify(event).respond("For more details on the commands, try !help [command], replacing \"[command]\" with the actual name of the command. Prefix all commands with !.");
        verify(event).respond("Here is a list of the dice systems I can handle");
        verify(event).respond("For more details on the dice systems, try !help [systemName], replacing \"[systemName]\" with the actual name of the system.");
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testBadMessage() {
        List<String> groups = Arrays.asList("!help badcommand", "badcommand");
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verifyNoMoreInteractions(event);
    }

    @Test
    public void testGetBaseMainHelp() {
        List<String> groups = new ArrayList<String>();
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Here is a list of the commands I can perform: help");
        verify(event).respond("For more details on the commands, try !help [command], replacing \"[command]\" with the actual name of the command. Prefix all commands with !.");
        verify(event).respond("Here is a list of the dice systems I can handle");
        verify(event).respond("For more details on the dice systems, try !help [systemName], replacing \"[systemName]\" with the actual name of the system.");
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testGetFilledMainHelp() {
        List<String> groups = new ArrayList<String>();
        commandHelpDetails.add(new HelpDetails("command", "command desc", Arrays.asList("example1", "example2")));
        rollerHelpDetails.add(new HelpDetails("roller", "roller desc", "roller", Arrays.asList("example1", "example2")));
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Here is a list of the commands I can perform: command, help");
        verify(event).respond("For more details on the commands, try !help [command], replacing \"[command]\" with the actual name of the command. Prefix all commands with !.");
        verify(event).respond("Here is a list of the dice systems I can handle: roller");
        verify(event).respond("For more details on the dice systems, try !help [systemName], replacing \"[systemName]\" with the actual name of the system.");
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testGetOverFilledMainHelp() {
        List<String> groups = new ArrayList<String>();
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand1", "command1 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand2", "command2 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand3", "command3 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand4", "command4 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand5", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand6", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand7", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand8", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand9", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand10", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand11", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand12", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand13", "command5 desc", Arrays.asList("example1", "example2")));
        commandHelpDetails.add(new HelpDetails("reallyreallyreallyreallyreallyreallylongcommand14", "command5 desc", Arrays.asList("example1", "example2")));
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Here is a list of the commands I can perform: help, reallyreallyreallyreallyreallyreallylongcommand1, reallyreallyreallyreallyreallyreallylongcommand10, reallyreallyreallyreallyreallyreallylongcommand11, reallyreallyreallyreallyreallyreallylongcommand12, reallyreallyreallyreallyreallyreallylongcommand13, reallyreallyreallyreallyreallyreallylongcommand14, reallyreallyreallyreallyreallyreallylongcommand2, reallyreallyreallyreallyreallyreallylongcommand3, reallyreallyreallyreallyreallyreallylongcommand4, reallyreallyreallyreallyreallyreallylongcommand5");
        verify(event).respond("reallyreallyreallyreallyreallyreallylongcommand6, reallyreallyreallyreallyreallyreallylongcommand7, reallyreallyreallyreallyreallyreallylongcommand8, reallyreallyreallyreallyreallyreallylongcommand9");
        verify(event).respond("For more details on the commands, try !help [command], replacing \"[command]\" with the actual name of the command. Prefix all commands with !.");
        verify(event).respond("Here is a list of the dice systems I can handle");
        verify(event).respond("For more details on the dice systems, try !help [systemName], replacing \"[systemName]\" with the actual name of the system.");
        verifyNoMoreInteractions(event);
    }

    @Test
    public void testGetCommandHelp() {
        List<String> groups = Arrays.asList("!help command", "command");
        commandHelpDetails.add(new HelpDetails("command", "command desc", Arrays.asList("example1", "example2")));
        rollerHelpDetails.add(new HelpDetails("roller", "roller desc", "roller", Arrays.asList("example1", "example2")));
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Help for the command command:");
        verify(event).respond("command desc");
        verify(event).respond("Examples:");
        verify(event).respond("example1");
        verify(event).respond("example2");
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testGetRollerHelp() {
        List<String> groups = Arrays.asList("!help roller", "roller");
        commandHelpDetails.add(new HelpDetails("command", "command desc", Arrays.asList("example1", "example2")));
        rollerHelpDetails.add(new HelpDetails("roller", "roller desc", "roller", Arrays.asList("example1", "example2")));
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Help for the roller roller:");
        verify(event).respond("roller desc");
        verify(event).respond("Examples:");
        verify(event).respond("example1");
        verify(event).respond("example2");
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testGetCommandHelpNoExamples() {
        List<String> groups = Arrays.asList("!help command", "command");
        commandHelpDetails.add(new HelpDetails("command", "command desc"));
        rollerHelpDetails.add(new HelpDetails("roller", "roller desc", "roller"));
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Help for the command command:");
        verify(event).respond("command desc");
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testGetRollerHelpNoExamples() {
        List<String> groups = Arrays.asList("!help roller", "roller");
        commandHelpDetails.add(new HelpDetails("command", "command desc"));
        rollerHelpDetails.add(new HelpDetails("roller", "roller desc", "roller"));
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        
        command.onSuccess(event, groups);
        
        verify(event).respond("Help for the roller roller:");
        verify(event).respond("roller desc");
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testBuildBaseRegexp() {
        assertEquals("!help( help)?", HelpCommand.buildRegexp(commandHelpDetails, rollerHelpDetails));
    }
    
    @Test
    public void testBuildRegexpWithCommand() {
        commandHelpDetails.add(new HelpDetails("command", "command desc"));
        assertEquals("!help( command| help)?", HelpCommand.buildRegexp(commandHelpDetails, rollerHelpDetails));
    }
    
    @Test
    public void testBuildRegexpWithRoller() {
        rollerHelpDetails.add(new HelpDetails("roller", "roller desc", "roller"));
        assertEquals("!help( roller| help)?", HelpCommand.buildRegexp(commandHelpDetails, rollerHelpDetails));
    }
    
    @Test
    public void testBuildRegexpWithCommandAndRoller() {
        commandHelpDetails.add(new HelpDetails("command", "command desc"));
        rollerHelpDetails.add(new HelpDetails("roller", "roller desc", "roller"));
        assertEquals("!help( command| roller| help)?", HelpCommand.buildRegexp(commandHelpDetails, rollerHelpDetails));
    }
    
    @Test
    public void testHelpDetails() {
        command = new HelpCommand(commandHelpDetails, rollerHelpDetails);
        HelpDetails details = command.getHelpDetails();
        assertEquals("help", details.getCommandName());
        assertEquals("Displays this help.", details.getDescription());
        assertEquals("command", details.getType());

        List<String> list = new ArrayList<>();
        assertEquals(list, details.getExamples());
    }
}
