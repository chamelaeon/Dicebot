package com.chamelaeon.dicebot.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

/**
 * A database of cards to draw from.
 * TODO: This should probably be an interface with an properties/xml implementation.
 * @author Chamelaeon
 */
public class CardBase {
	/** The JSON object tree of cards. */
    private JsonNode cardTree;
	/** The Random. */
	private final Random random;
	/** The ranges for each rarity type. */
	private final RangeMap<Integer, JsonNode> rarityRangeMap;
	
	/**
	 * Constructor.
	 * @param stream The input stream to the XML of the cards.
	 * @throws IOException if there is an error reading the stream.
	 * @throws JsonProcessingException 
	 */
	public CardBase(InputStream stream) throws IOException {
	    ObjectMapper mapper = new ObjectMapper();
		random = new Random();
		rarityRangeMap = TreeRangeMap.create();
	    cardTree = mapper.readTree(stream);
	    
	    JsonNode rarities = cardTree.get("Rarity");
	    int totalPercent = 0;
	    for (JsonNode rarityNode : rarities) {
            int rarityPercent = rarityNode.get("Percent").getIntValue();
            rarityRangeMap.put(Range.closedOpen(totalPercent, totalPercent + rarityPercent), rarityNode);
            
            totalPercent += rarityPercent;
        }
	    
	    if (totalPercent != 100) {
	        throw new IllegalArgumentException("Card percentages must sum to to 100!");
	    }
	}
	
	/**
	 * Returns the range map that was loaded. Useful for testing, if it's useful for anything else
	 * it should be made public.
	 * @return the range map.
	 */
	protected RangeMap<Integer, JsonNode> getRarityMap() {
	    return rarityRangeMap;
	}
	
	/**
	 * Draws a card.
	 * @return The drawn card.
	 */
	public String draw() {
	    // First, determine what rarity.
	    int rarity = random.nextInt(100);
	    JsonNode rarityNode = rarityRangeMap.get(rarity);
	    
	    // Fetch all cards in the rarity, then pick one randomly.
	    JsonNode cards = rarityNode.get("Card");
        int randomIndex = random.nextInt(cards.size());
        JsonNode card = cards.get(randomIndex);
        
        // Assemble the output. TODO: Personality file this?
        String title = card.get("Title").getTextValue();
        String cardText = card.get("Text").getTextValue();
        return title + " (" + rarityNode.get("Name").getTextValue() + ") - " + cardText;
	}
}
