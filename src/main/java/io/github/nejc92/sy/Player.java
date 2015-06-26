package io.github.nejc92.sy;

import io.github.nejc92.mcts.MctsDomainAgent;

public class Player implements MctsDomainAgent<GameState> {

    @Override
    public GameState getTerminalStateByPerformingSimulationFromState(GameState gameState) {
        return null;
    }

    @Override
    public double getRewardFromTerminalState(GameState gameState) {
        return 0;
    }
}