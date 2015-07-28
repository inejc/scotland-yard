package io.github.nejc92.sy.game;

import io.github.nejc92.sy.game.board.Connection;
import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.players.RandomHider;
import io.github.nejc92.sy.players.RandomSeeker;
import io.github.nejc92.sy.players.Seeker;
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
        players[0] = new RandomHider(Player.Operator.COMPUTER);
        players[1] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.BLACK);
        players[2] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.BLUE);
        players[3] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.YELLOW);
        players[4] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.RED);
        players[5] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.GREEN);
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

    @Test
    public void testSeekerOnHidersMostProbablePosition() {
        assertFalse(playersOnBoard.seekerOnHidersMostProbablePosition());
        playersOnBoard.movePlayerFromActualPosition(2, new Action(Connection.Transportation.TAXI, 103));
        assertTrue(playersOnBoard.seekerOnHidersMostProbablePosition());
    }

    @Test
    public void testSeekerOnHidersActualPosition() {
        assertFalse(playersOnBoard.seekerOnHidersActualPosition());
        playersOnBoard.movePlayerFromActualPosition(2, new Action(Connection.Transportation.TAXI, 34));
        assertTrue(playersOnBoard.seekerOnHidersActualPosition());
    }

    @Test
    public void testGetAvailableActionsFromSeekersPov() {
        List<Action> availableActions = new ArrayList<>(Arrays.asList(
                new Action(Connection.Transportation.TAXI, 85),
                new Action(Connection.Transportation.TAXI, 86),
                new Action(Connection.Transportation.TAXI, 102)
        ));
        assertEquals(availableActions, playersOnBoard.getAvailableActionsFromSeekersPov(0));
        availableActions = new ArrayList<>(Arrays.asList(
                new Action(Connection.Transportation.TAXI, 75),
                new Action(Connection.Transportation.TAXI, 93),
                new Action(Connection.Transportation.TAXI, 95),
                new Action(Connection.Transportation.BUS, 74),
                new Action(Connection.Transportation.BUS, 77),
                new Action(Connection.Transportation.BUS, 93)
        ));
        assertEquals(availableActions, playersOnBoard.getAvailableActionsFromSeekersPov(1));
    }

    @Test
    public void testMovePlayerFromActualPosition() {

    }

    @Test
    public void testMovePlayerFromSeekersPov() {

    }

    @Test
    public void testSetHidersActualAsMostProbablePosition() throws Exception {

    }

    @Test
    public void testRecalculateHidersMostProbablePosition() throws Exception {

    }

    @Test
    public void testRemoveCurrentSeekersPositionFromPossibleHidersPositions() throws Exception {

    }
}