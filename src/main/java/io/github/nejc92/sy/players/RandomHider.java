package io.github.nejc92.sy.players;

import io.github.nejc92.sy.Game.State;

public class RandomHider extends Player {

    public RandomHider(int boardPosition) {
        super(Type.HIDER, boardPosition);
    }

    public boolean isOnPosition(int boardPosition) {
        return this.getBoardPosition() == boardPosition;
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        return 0;
    }
}