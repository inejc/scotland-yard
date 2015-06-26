package io.github.nejc92.sy;

import io.github.nejc92.mcts.MctsDomainState;

import java.util.List;

public class GameState implements MctsDomainState<String, Player> {

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
    public List<String> getAvailableActionsForCurrentAgent() {
        return null;
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(String s) {
        return null;
    }
}