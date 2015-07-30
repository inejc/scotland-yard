package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;
import io.github.nejc92.sy.game.State;

import java.util.List;

public class BiasedHider extends Hider {

    public BiasedHider(Operator operator) {
        super(operator);
    }

    @Override
    protected Action getHidersActionFromStatesAvailableActionsForSimulation(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        if (availableActions.size() > 0)
            return getBiasedForSeekerActionFromActions(availableActions, state.getPlayersOnBoard());
        else
            return null;
    }

    @Override
    protected Action getSeekersActionFromStatesAvailableActionsForSimulation(State state) {
        return null;
    }

    private Action getBiasedForSeekerActionFromActions(List<Action> actions, PlayersOnBoard playersOnBoard) {
        return actions.stream()
                .min((action1, action2) -> Integer.compare(
                        playersOnBoard.shortestDistanceBetweenPositionAndHidersMostProbablePosition(
                                action1.getDestination()),
                        playersOnBoard.shortestDistanceBetweenPositionAndHidersMostProbablePosition(
                                action2.getDestination())))
                .get();
    }
}