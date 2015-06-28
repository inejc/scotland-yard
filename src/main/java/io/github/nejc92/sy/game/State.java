package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.players.RandomHider;
import io.github.nejc92.sy.players.RandomSeeker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class State implements MctsDomainState<Action, Player> {

    private static final int NUMBER_OF_PLAYERS = 6;
    private static final List<Integer> STARTING_POSITIONS = new ArrayList<>(
        Arrays.asList(13, 26, 34, 50, 53, 62, 91, 94, 103, 112, 117, 132, 138, 141, 155, 174, 197, 198));

    private final Board board;
    private final Player[] players;
    private int currentPlayerIndex;
    private int previousPlayerIndex;

    public static State initialize() {
        Board board = Board.initialize();
        Player[] players = initializePlayers();
        return new State(board, players);
    }

    private static Player[] initializePlayers() {
        Collections.shuffle(STARTING_POSITIONS);
        return new Player[] {
            new RandomSeeker(RandomSeeker.Color.BLUE, STARTING_POSITIONS.get(0)),
            new RandomSeeker(RandomSeeker.Color.YELLOW, STARTING_POSITIONS.get(1)),
            new RandomSeeker(RandomSeeker.Color.RED, STARTING_POSITIONS.get(2)),
            new RandomSeeker(RandomSeeker.Color.GREEN, STARTING_POSITIONS.get(3)),
            new RandomSeeker(RandomSeeker.Color.BLACK, STARTING_POSITIONS.get(4)),
            new RandomHider(STARTING_POSITIONS.get(5))
        };
    }

    private State(Board board, Player[] players) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = 5;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public Player getCurrentAgent() {
        return players[currentPlayerIndex];
    }

    @Override
    public Player getPreviousAgent() {
        return players[previousPlayerIndex];
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        //int currentPlayerPosition = getCurrentAgent().getBoardPosition();
        //return board.getPossibleActionsForPosition(currentPlayerPosition);
        return null;
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        validateIsAvailableAction(action);
        // remove transportation card
        //getCurrentAgent().setBoardPosition(action.getDestination());
        selectNextPlayer();
        return this;
    }

    private void validateIsAvailableAction(Action action) {
        if (!getAvailableActionsForCurrentAgent().contains(action)) {
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
        }
    }

    private void selectNextPlayer() {
        currentPlayerIndex = ++currentPlayerIndex % NUMBER_OF_PLAYERS;
        previousPlayerIndex = ++previousPlayerIndex % NUMBER_OF_PLAYERS;
    }
}