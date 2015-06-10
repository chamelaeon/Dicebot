package com.chamelaeon.dicebot.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chamelaeon.dicebot.api.HelpDetails;

public class ChangelogCommandTest extends CommandTestBase {

    ChangelogCommand command;
    
    @Before
    public void setUp() throws Exception {
        doMockSetup();
        command = new ChangelogCommand();
    }
    
    @After
    public void tearDown() throws Exception {
        doMockTeardown();
    }

    @Test
    public void testChangelog() {
        List<String> groups = Arrays.asList("!changelog", "");
        command.onSuccess(event, groups);
        
        verify(event).respond(startsWith("Changes for version 1.3.0"));
    }
    
    @Test
    public void testChangelogWithGoodVersion() {
        List<String> groups = Arrays.asList("!changelog", "1.0.0");
        command.onSuccess(event, groups);
        
        verify(event).respond(startsWith("Changes for version 1.0.0"));
    }
    
    @Test
    public void testChangelogWithBadVersion() {
        List<String> groups = Arrays.asList("!changelog", "potatoes");
        command.onSuccess(event, groups);
        
        verifyNoMoreInteractions(event);
    }
    
    @Test
    public void testHelpDetails() {
        HelpDetails details = command.getHelpDetails();
        assertEquals("changelog", details.getCommandName());
        assertEquals("Displays the latest changes for the bot. If a version is provided, displays the changes for that version.", 
                details.getDescription());
        assertEquals("command", details.getType());

        List<String> list = new ArrayList<>();
        list.add("!changelog");
        list.add("!changelog 1.2.0");
        assertEquals(list, details.getExamples());
    }
}