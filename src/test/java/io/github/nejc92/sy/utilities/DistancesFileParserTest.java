package io.github.nejc92.sy.utilities;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DistancesFileParserTest {

    private static final String FILE_NAME = "src/main/resources/hiders_distances_file.xml";

    DistancesFileParser distancesFileParser;

    @Test
    public void testGetParsedData() {
        distancesFileParser = new DistancesFileParser(FILE_NAME);
        List<List<Integer>> distances = distancesFileParser.getParsedData();
        int position1 = 5;
        int position2 = 45;
        int index1 = position1 - 1;
        int index2 = (position2 - position1) - 1;
        int distance = distances.get(index1).get(index2);
        assertEquals(5, distance);
        position1 = 160;
        position2 = 198;
        index1 = position1 - 1;
        index2 = (position2 - position1) - 1;
        distance = distances.get(index1).get(index2);
        assertEquals(3, distance);
    }
}