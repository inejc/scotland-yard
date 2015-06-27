package io.github.nejc92.sy;

import io.github.nejc92.sy.Game.Action;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BoardFileParserTest {

    private static final String TEST_BOARD_FILE_NAME = "src/test/resources/test_board_file.xml";
    private final BoardFileParser parser = new BoardFileParser(TEST_BOARD_FILE_NAME);

    @Test
    public void testGetParsedDataLength() {
        List<List<Action>> boardPositions = parser.getParsedData();
        assertEquals(2, boardPositions.size());
        assertEquals(3, boardPositions.get(0).size());
        assertEquals(3, boardPositions.get(1).size());
    }

    @Test
    public void testGetParsedDataPosition1Actions() {
        List<List<Action>> boardPositions = parser.getParsedData();
        assertEquals(Action.Transportation.TAXI, boardPositions.get(0).get(0).transportation);
        assertEquals(2, boardPositions.get(0).get(0).destination);
        assertEquals(Action.Transportation.BUS, boardPositions.get(0).get(1).transportation);
        assertEquals(3, boardPositions.get(0).get(1).destination);
        assertEquals(Action.Transportation.UNDERGROUND, boardPositions.get(0).get(2).transportation);
        assertEquals(6, boardPositions.get(0).get(2).destination);
    }

    @Test
    public void testGetParsedDataPosition2Actions() {
        List<List<Action>> boardPositions = parser.getParsedData();
        assertEquals(Action.Transportation.TAXI, boardPositions.get(1).get(0).transportation);
        assertEquals(1, boardPositions.get(1).get(0).destination);
        assertEquals(Action.Transportation.BUS, boardPositions.get(1).get(1).transportation);
        assertEquals(4, boardPositions.get(1).get(1).destination);
        assertEquals(Action.Transportation.BOAT, boardPositions.get(1).get(2).transportation);
        assertEquals(5, boardPositions.get(1).get(2).destination);

    }
}