package io.github.nejc92.sy.game;

import io.github.nejc92.sy.players.*;
import io.github.nejc92.sy.strategies.CoalitionReduction;
import io.github.nejc92.sy.strategies.MoveFiltering;
import io.github.nejc92.sy.strategies.Playouts;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PlayersOnBoardTest {

    private static final Player[] PLAYERS = initializePlayers();
    private static final int[] PLAYERS_POSITIONS = {34, 94, 155, 13, 197, 53};
    private static final int HIDERS_MOST_PROBABLE_POSITION = 103;

    private PlayersOnBoard playersOnBoard;

    @Before
    public void setUp() throws Exception {
        playersOnBoard = PlayersOnBoard.initializeTest(PLAYERS, PLAYERS_POSITIONS, HIDERS_MOST_PROBABLE_POSITION);
    }

    private static Player[] initializePlayers() {
        Player[] players = new Player[6];
        players[0] = new Hider(Player.Operator.MCTS, "", Playouts.Uses.GREEDY,
                CoalitionReduction.Uses.YES, MoveFiltering.Uses.YES);
        players[1] = new Seeker(Player.Operator.MCTS, Seeker.Color.BLACK.name(), Playouts.Uses.GREEDY,
                CoalitionReduction.Uses.YES, MoveFiltering.Uses.YES);
        players[2] = new Seeker(Player.Operator.MCTS, Seeker.Color.BLUE.name(), Playouts.Uses.GREEDY,
                CoalitionReduction.Uses.YES, MoveFiltering.Uses.YES);
        players[3] = new Seeker(Player.Operator.MCTS, Seeker.Color.YELLOW.name(), Playouts.Uses.GREEDY,
                CoalitionReduction.Uses.YES, MoveFiltering.Uses.YES);
        players[4] = new Seeker(Player.Operator.MCTS, Seeker.Color.RED.name(), Playouts.Uses.GREEDY,
                CoalitionReduction.Uses.YES, MoveFiltering.Uses.YES);
        players[5] = new Seeker(Player.Operator.MCTS, Seeker.Color.GREEN.name(), Playouts.Uses.GREEDY,
                CoalitionReduction.Uses.YES, MoveFiltering.Uses.YES);
        return players;
    }

    @Test
    public void testGetNumberOfPlayers() {
        assertEquals(6, playersOnBoard.getNumberOfPlayers());
    }

    @Test
    public void testPlayerIsHider() {
        assertTrue(playersOnBoard.playerIsHider(0));
        assertFalse(playersOnBoard.playerIsHider(1));
        assertFalse(playersOnBoard.playerIsHider(2));
        assertFalse(playersOnBoard.playerIsHider(3));
        assertFalse(playersOnBoard.playerIsHider(4));
        assertFalse(playersOnBoard.playerIsHider(5));
    }

    @Test
    public void testGetPlayerAtIndex() {
        assertEquals(PLAYERS[0], playersOnBoard.getPlayerAtIndex(0));
        assertEquals(PLAYERS[1], playersOnBoard.getPlayerAtIndex(1));
        assertEquals(PLAYERS[2], playersOnBoard.getPlayerAtIndex(2));
        assertEquals(PLAYERS[3], playersOnBoard.getPlayerAtIndex(3));
        assertEquals(PLAYERS[4], playersOnBoard.getPlayerAtIndex(4));
        assertEquals(PLAYERS[5], playersOnBoard.getPlayerAtIndex(5));
    }

//    @Test
//    public void testSeekerOnHidersMostProbablePosition() {
//        assertFalse(playersOnBoard.anySeekerOnHidersMostProbablePosition());
//        playersOnBoard.movePlayerFromActualPosition(2, new Action(Action.Transportation.TAXI, 103));
//        assertTrue(playersOnBoard.anySeekerOnHidersMostProbablePosition());
//    }

    @Test
    public void testSeekerOnHidersActualPosition() {
        assertFalse(playersOnBoard.anySeekerOnHidersActualPosition());
        playersOnBoard.movePlayerFromActualPosition(2, new Action(Action.Transportation.TAXI, 34));
        assertTrue(playersOnBoard.anySeekerOnHidersActualPosition());
    }

    @Test
    public void testGetAvailableActionsFromSeekersPov() {
        List<Action> availableActions = new ArrayList<>(Arrays.asList(
                new Action(Action.Transportation.TAXI, 85),
                new Action(Action.Transportation.TAXI, 86),
                new Action(Action.Transportation.TAXI, 102)
        ));
        assertEquals(availableActions, playersOnBoard.getAvailableActionsFromSeekersPov(0));
        availableActions = new ArrayList<>(Arrays.asList(
                new Action(Action.Transportation.TAXI, 75),
                new Action(Action.Transportation.TAXI, 93),
                new Action(Action.Transportation.TAXI, 95),
                new Action(Action.Transportation.BUS, 74),
                new Action(Action.Transportation.BUS, 77),
                new Action(Action.Transportation.BUS, 93)
        ));
        assertEquals(availableActions, playersOnBoard.getAvailableActionsFromSeekersPov(1));
    }

//    @Test
//    public void testMovePlayerFromActualPosition() {
//
//    }
//
//    @Test
//    public void testMovePlayerFromSeekersPov() {
//
//    }
//
//    @Test
//    public void testSetHidersActualAsMostProbablePosition() throws Exception {
//
//    }
//
//    @Test
//    public void testRecalculateHidersMostProbablePosition() throws Exception {
//
//    }
//
//    @Test
//    public void testRemoveCurrentSeekersPositionFromPossibleHidersPositions() throws Exception {
//
//    }
}