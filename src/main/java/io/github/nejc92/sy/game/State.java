package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.players.Player;

import java.util.List;

public class State implements MctsDomainState<Action, Player> {

    private static final int NUMBER_OF_ROUNDS = 24;

    private final Board board;
    private final Player[] players;
    private final int numberOfPlayers;
    private int currentPlayerIndex;
    private int previousPlayerIndex;

    public static State initialize(Player[] players) {
        Board board = Board.initialize();
        return new State(board, players);
    }

    private State(Board board, Player[] players) {
        this.board = board;
        this.players = players;
        this.numberOfPlayers = players.length;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = numberOfPlayers - 1;
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
        if (!isAvailableAction(action)) {
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
        }
    }

    private boolean isAvailableAction(Action action) {
        return getAvailableActionsForCurrentAgent().contains(action);
    }

    private void selectNextPlayer() {
        currentPlayerIndex = ++currentPlayerIndex % numberOfPlayers;
        previousPlayerIndex = ++previousPlayerIndex % numberOfPlayers;
    }
}