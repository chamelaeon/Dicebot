package com.chamelaeon.dicebot.personality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class TokenSubstitutionTest {

    TokenSubstitution tokenSub;
    String token = "%TOKEN%";
    String substitution = "sub";
    
    @Before
    public void setUp() throws Exception {
        tokenSub = new TokenSubstitution(token, substitution);
    }

    @Test
    public void testTokenSubstitutionStringObject() {
        tokenSub = new TokenSubstitution(token, new Integer(5));
        assertEquals("5", tokenSub.getSubstitution());
    }

    @Test
    public void testGetToken() {
        assertSame(token, tokenSub.getToken());
    }

    @Test
    public void testGetSubstitution() {
        assertSame(substitution, tokenSub.getSubstitution());
    }
    
    @Test
    public void testEquals() {
        TokenSubstitution tokenSub2 = new TokenSubstitution(token, substitution);
        assertEquals(tokenSub, tokenSub2);
    };
    
    @Test
    public void testEqualsSame() {
        assertEquals(tokenSub, tokenSub);
    };
    
    @Test
    public void testNotEquals() {
        TokenSubstitution tokenSub2 = new TokenSubstitution("wark", "bad");
        assertNotEquals(tokenSub, null);
        assertNotEquals(tokenSub, "bad");
        assertNotEquals(tokenSub, tokenSub2);
    };
    
    @Test
    public void testEqualsEmpty() {
        TokenSubstitution tokenSub2 = new TokenSubstitution(null, null);
        TokenSubstitution tokenSub3 = new TokenSubstitution(token, null);
        TokenSubstitution tokenSub4 = new TokenSubstitution(null, substitution);
        assertNotEquals(tokenSub, tokenSub2);
        assertNotEquals(tokenSub2, tokenSub);
        assertNotEquals(tokenSub, tokenSub3);
        assertNotEquals(tokenSub3, tokenSub);
        assertNotEquals(tokenSub, tokenSub4);
        assertNotEquals(tokenSub4, tokenSub);
        assertEquals(tokenSub2, tokenSub2);
    };
    
    @Test 
    public void testHashCode() {
        TokenSubstitution tokenSub2 = new TokenSubstitution(token, substitution);
        assertEquals(tokenSub.hashCode(), tokenSub2.hashCode());
    }
    
    @Test 
    public void testHashCodeNotEquals() {
        TokenSubstitution tokenSub2 = new TokenSubstitution("wark", "bad");
        assertNotEquals(tokenSub.hashCode(), tokenSub2.hashCode());
    }
}
