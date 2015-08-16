package io.github.nejc92.sy.strategies;

import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.players.Seeker;

public class CoalitionReduction {

    public enum Uses {
        YES, NO
    }

    private static final double COALITION_REDUCTION_PARAMETER = 0.25;

    public static double getCoalitionReductionRewardFromTerminalState(State state, Seeker seeker) {
        if (state.seekerWon(seeker))
            return 1;
        else if (state.seekersWon())
            return 1 - COALITION_REDUCTION_PARAMETER;
        else
            return 0;
    }

    public static double getNormalRewardFromTerminalState(State state) {
        if (state.seekersWon())
            return 1;
        else
            return 0;
    }
}