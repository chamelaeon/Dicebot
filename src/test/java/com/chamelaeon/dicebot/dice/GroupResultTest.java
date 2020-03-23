package com.chamelaeon.dicebot.dice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GroupResultTest {

    GroupResult result;
    private List<DieResult> dice = new ArrayList<DieResult>();
    private long natural = 1;
    private long modified = 2;
    private boolean criticalFailure = true;
    private boolean criticalSuccess = false;

    @Before
    public void setUp() throws Exception {
        result = new GroupResult(dice, natural, modified);
    }

    @Test
    public void testGetDice() {
        assertSame(dice, result.getDice());
    }

    @Test
    public void testGetNatural() {
        assertSame(natural, result.getNatural());
    }

    @Test
    public void testGetModified() {
        assertSame(modified, result.getModified());
    }
}
