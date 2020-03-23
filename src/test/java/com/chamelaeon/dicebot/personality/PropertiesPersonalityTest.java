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
        propsPersonality = new PropertiesPersonality(props);
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
        props.put("UseCriticalSuccessMessages", "True");
        props.put("UseCriticalFailureMessages", "True");
        props.put("CriticalSuccesses", "CritSucc1#CritSucc2");
        props.put("CriticalFailures", "CritFail1#CritFail2");
        propsPersonality = new PropertiesPersonality(props);

        assertEquals(2, propsPersonality.rollResultMessageLists.get("criticalSuccess").size());
        assertEquals("CritSucc1", propsPersonality.rollResultMessageLists.get("criticalSuccess").get(0));
        assertEquals("CritSucc2", propsPersonality.rollResultMessageLists.get("criticalSuccess").get(1));

        assertEquals(2, propsPersonality.rollResultMessageLists.get("criticalFailure").size());
        assertEquals("CritFail1", propsPersonality.rollResultMessageLists.get("criticalFailure").get(0));
        assertEquals("CritFail2", propsPersonality.rollResultMessageLists.get("criticalFailure").get(1));
    }
}
