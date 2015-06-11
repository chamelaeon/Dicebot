package com.chamelaeon.dicebot.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.HelpDetails;

public class VersionCommandTest extends CommandTestBase {

    VersionCommand command;
    String version = "1.0.0";
    
    @Before
    public void setUp() throws Exception {
        doMockSetup();
        command = new VersionCommand(version);
    }
    
    @After
    public void tearDown() throws Exception {
        doMockTeardown();
    }

    @Test
    public void testVersion() {
        List<String> groups = new ArrayList<String>();
        command.onSuccess(event, groups);
        
        verify(event).respond("This dicebot is running version 1.0.0.");
    }
    
    @Test
    public void testHelpDetails() {
        HelpDetails details = command.getHelpDetails();
        assertEquals("version", details.getCommandName());
        assertEquals("Prints the version this bot is running.", 
                details.getDescription());
        assertEquals("command", details.getType());

        List<String> list = new ArrayList<>();
        assertEquals(list, details.getExamples());
    }
}