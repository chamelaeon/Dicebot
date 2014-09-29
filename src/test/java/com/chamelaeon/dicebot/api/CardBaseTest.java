package com.chamelaeon.dicebot.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.RangeMap;

public class CardBaseTest {

    CardBase cardBase;
    
    @Before
    public void setUp() throws IOException {
        cardBase = new CardBase(CardBaseTest.class.getResourceAsStream("dramaCards.json"));
    }
    
    @Test
    public void testLoad() throws IOException {
        RangeMap<Integer, JsonNode> rarityMap = cardBase.getRarityMap();
        
        assertNull(rarityMap.get(-1));
        assertEquals("Common", rarityMap.get(0).get("Name").getTextValue());
        assertEquals("Common", rarityMap.get(49).get("Name").getTextValue());
        assertEquals("Uncommon", rarityMap.get(50).get("Name").getTextValue());
        assertEquals("Uncommon", rarityMap.get(79).get("Name").getTextValue());
        assertEquals("Rare", rarityMap.get(80).get("Name").getTextValue());
        assertEquals("Rare", rarityMap.get(94).get("Name").getTextValue());
        assertEquals("Mythic", rarityMap.get(95).get("Name").getTextValue());
        assertEquals("Mythic", rarityMap.get(99).get("Name").getTextValue());
        assertNull(rarityMap.get(100));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testLoadDoesNotSumTo100() throws IOException {
        cardBase = new CardBase(CardBaseTest.class.getResourceAsStream("dramaCardsDoesNotSumTo100.json"));
    }
    
    @Test(expected = IOException.class)
    public void testLoadWithInvalidStream() throws IOException {
        cardBase = new CardBase(CardBaseTest.class.getResourceAsStream("doesnotexist.json"));
    }
    
    @Test
    public void testDraw() {
        String card = "";
        
        while (!card.startsWith("Test common card.")) {
            card = cardBase.draw();
        }
        assertEquals("Test common card. (Common) - Test common description.", card);
    }
    
    @Test
    public void testDrawProbability() {
        double commonCount = 0;
        double uncommonCount = 0;
        double rareCount = 0;
        double mythicCount = 0;
        double cardsToDraw = 1000000;
        
        for (int i = 0; i < cardsToDraw; i++) {
            String card = cardBase.draw();
            
            if (card.contains("Common")) {
                commonCount++;
            } else if (card.contains("Uncommon")) {
                uncommonCount++;
            } else if (card.contains("Rare")) {
                rareCount++;
            } else if (card.contains("Mythic")) {
                mythicCount++;
            } else {
                fail("Encountered unknown card type!");
            }
        }
        
        assertEquals(.5, commonCount / cardsToDraw, 0.001);
        assertEquals(.3, uncommonCount / cardsToDraw, 0.001);
        assertEquals(.15, rareCount / cardsToDraw, 0.001);
        assertEquals(.05, mythicCount / cardsToDraw, 0.001);
    }
}
