package io.github.nejc92.sy;

import io.github.nejc92.mcts.MctsDomainState;

import java.util.List;

public class State implements MctsDomainState<Action, Player> {

    private final Board board;

    public static State initialize() {
        Board board = Board.initialize();
        return new State(board);
    }

    private State(Board board) {
        this.board = board;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public Player getCurrentAgent() {
        return null;
    }

    @Override
    public Player getPreviousAgent() {
        return null;
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return 0;
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        return null;
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        return null;
    }
}