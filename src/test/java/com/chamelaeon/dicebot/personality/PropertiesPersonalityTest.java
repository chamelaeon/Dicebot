package com.chamelaeon.dicebot.personality;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class PropertiesPersonalityTest {

    PropertiesPersonality propsPersonality;
    
    @Before
    public void setUp() throws Exception {
        Properties props = new Properties();
        props.put("TestKey", "TestValue");
        propsPersonality = new PropertiesPersonality(props, false, false);
    }

    @Test
    public void testPropertiesPersonality() {
        assertEquals(1, propsPersonality.outputTexts.size());
        assertEquals("TestValue", propsPersonality.outputTexts.get("TestKey"));
    }

    @Test
    public void testPropertiesPersonalityWithCrits() {
        Properties props = new Properties();
        props.put("TestKey", "TestValue");
        props.put("CriticalSuccesses", "CritSucc1#CritSucc2");
        props.put("CriticalFailures", "CritFail1#CritFail2");
        propsPersonality = new PropertiesPersonality(props, true, true);
        
        assertEquals(2, propsPersonality.criticalSuccesses.size());
        assertEquals("CritSucc1", propsPersonality.criticalSuccesses.get(0));
        assertEquals("CritSucc2", propsPersonality.criticalSuccesses.get(1));
        
        assertEquals(2, propsPersonality.criticalFailures.size());
        assertEquals("CritFail1", propsPersonality.criticalFailures.get(0));
        assertEquals("CritFail2", propsPersonality.criticalFailures.get(1));
    }
}
