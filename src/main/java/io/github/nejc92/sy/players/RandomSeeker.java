package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;

import java.util.Collections;
import java.util.List;

public class RandomSeeker extends Seeker {

    public RandomSeeker(Operator operator, Seeker.Color color) {
        super(operator, color);
    }

    @Override
    protected Action getActionFromStatesAvailableActionsForSimulation(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        Collections.shuffle(availableActions);
        return availableActions.get(0);
    }
}