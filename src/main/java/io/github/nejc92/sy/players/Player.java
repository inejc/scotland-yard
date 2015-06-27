package io.github.nejc92.sy.players;

import io.github.nejc92.mcts.MctsDomainAgent;
import io.github.nejc92.sy.Game.Action;
import io.github.nejc92.sy.Game.State;

import java.util.Collections;
import java.util.List;

public abstract class Player implements MctsDomainAgent<State> {

    public enum Type {
        HIDER, SEEKER
    }

    private Type type;
    private int boardPosition;

    protected Player(Type type, int boardPosition) {
        this.boardPosition = boardPosition;
        this.type = type;
    }

    public int getBoardPosition() {
        return boardPosition;
    }

    public void setBoardPosition(int newBoardPosition) {
        boardPosition = newBoardPosition;
    }

    public Type getType() {
        return type;
    }

    @Override
    public final State getTerminalStateByPerformingSimulationFromState(State state) {
        while (!state.isTerminal()) {
            Action randomAction = getActionFromStatesAvailableActions(state);
            state.performActionForCurrentAgent(randomAction);
        }
        return state;
    }

    private Action getActionFromStatesAvailableActions(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        Collections.shuffle(availableActions);
        return availableActions.get(0);
    }
}