package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.State;

public class RandomSeekerCoalitionReduction extends RandomSeeker {

    private static final double COALITION_REDUCTION_PARAMETER = 0.25;

    public RandomSeekerCoalitionReduction(Operator operator, Seeker.Color color) {
        super(operator, color);
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        if (state.seekerWon(this))
            return 1;
        else if (state.seekersWon())
            return 1 - COALITION_REDUCTION_PARAMETER;
        else
            return 0;
    }
}