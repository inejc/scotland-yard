package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.playouts.RandomPlayout;

public class RandomHider extends Hider {

    public RandomHider(Operator operator) {
        super(operator);
    }

    @Override
    protected Action getActionForHiderFromStatesAvailableActionsForSimulation(State state) {
        return RandomPlayout.getRandomAction(state);
    }

    @Override
    protected Action getActionForSeekerFromStatesAvailableActionsForSimulation(State state) {
        return RandomPlayout.getRandomAction(state);
    }
}