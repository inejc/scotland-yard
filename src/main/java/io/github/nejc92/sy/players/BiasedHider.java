package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;

import java.util.List;

public class BiasedHider extends Hider {

    public BiasedHider(Operator operator) {
        super(operator);
    }

    @Override
    protected Action getActionFromStatesAvailableActionsForSimulation(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        if (availableActions.size() > 0)
            return getBiasedForHiderActionFromActions(availableActions);
        else
            return null;
    }

    private Action getBiasedForHiderActionFromActions(List<Action> actions) {
        return null;
    }
}