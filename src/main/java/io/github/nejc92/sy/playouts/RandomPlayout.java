package io.github.nejc92.sy.playouts;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;

import java.util.Collections;
import java.util.List;

public class RandomPlayout {

    public static Action getRandomAction(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        Collections.shuffle(availableActions);
        if (availableActions.size() > 0)
            return availableActions.get(0);
        else
            return null;
    }
}