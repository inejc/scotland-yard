package io.github.nejc92.sy.game.board;

import io.github.nejc92.sy.game.Action;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BoardTest {


    private static final List<Integer> DESTINATIONS_163 = new ArrayList<>(
            Arrays.asList(146, 177, 144, 176, 191, 111, 153));

    private static final List<Integer> DESTINATIONS_163_TAXI = new ArrayList<>(
            Arrays.asList(146, 177));

    private static final List<Action> ACTIONS_157 = new ArrayList<>(Arrays.asList(
            new Action(Connection.Transportation.TAXI, 156),
            new Action(Connection.Transportation.TAXI, 158),
            new Action(Connection.Transportation.TAXI, 170),
            new Action(Connection.Transportation.BUS, 156),
            new Action(Connection.Transportation.BUS, 133),
            new Action(Connection.Transportation.BUS, 142),
            new Action(Connection.Transportation.BUS, 185),
            new Action(Connection.Transportation.BLACK_FARE, 115),
            new Action(Connection.Transportation.BLACK_FARE, 194)));

    private static final List<Action> ACTIONS_157_BUS = new ArrayList<>(Arrays.asList(
            new Action(Connection.Transportation.BUS, 156),
            new Action(Connection.Transportation.BUS, 133),
            new Action(Connection.Transportation.BUS, 142),
            new Action(Connection.Transportation.BUS, 185)));

    private Board board = Board.initialize();

    @Test
    public void testGetDestinationsForPosition() {
        List<Integer> destinations = board.getDestinationsForPosition(163);
        assertEquals(DESTINATIONS_163, destinations);
    }

    @Test
    public void testGetActionsForPosition() {
        List<Action> actions = board.getActionsForPosition(157);
        assertEquals(ACTIONS_157, actions);
    }

    @Test
    public void testGetTransportationDestinationsForPosition() {
        List<Integer> destinations = board.getTransportationDestinationsForPosition(
                Connection.Transportation.TAXI, 163);
        assertEquals(DESTINATIONS_163_TAXI, destinations);
    }

    @Test
    public void testGetTransportationActionsForPosition() {
        List<Action> actions = board.getTransportationActionsForPosition(Connection.Transportation.BUS, 157);
        assertEquals(ACTIONS_157_BUS, actions);
    }

    @Test
    public void testShortestDistanceBetween() {
        int shortestDistance = board.shortestDistanceBetween(66, 124);
        assertEquals(3, shortestDistance);
        shortestDistance = board.shortestDistanceBetween(189, 163);
        assertEquals(2, shortestDistance);
        shortestDistance = board.shortestDistanceBetween(130, 63);
        assertEquals(4, shortestDistance);
    }
}