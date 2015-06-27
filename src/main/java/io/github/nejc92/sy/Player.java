package io.github.nejc92.sy;

import io.github.nejc92.mcts.MctsDomainAgent;

public class Player implements MctsDomainAgent<State> {

    @Override
    public State getTerminalStateByPerformingSimulationFromState(State state) {
        return null;
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        return 0;
    }
}