package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;

import java.util.List;

public class BiasedHider extends RandomHider {

    public BiasedHider(Operator operator) {
        super(operator);
    }

    @Override
    protected Action getActionFromStatesAvailableActionsForSimulation(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        if (availableActions.size() > 0)
            return biased;
        else
            return null;
    }
}