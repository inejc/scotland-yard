package io.github.nejc92.sy.playouts;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;
import io.github.nejc92.sy.game.State;

import java.util.List;

public class BiasedPlayout {

    public static Action getBiasedActionForHider(State state) {
        List<Action> actions = state.getAvailableActionsForCurrentAgent();
        if (actions.size() > 0)
            return getBiasedActionForHiderConfidently(actions, state.getPlayersOnBoard());
        else
            return null;
    }

    public static Action getBiasedActionForSeeker(State state) {
        List<Action> actions = state.getAvailableActionsForCurrentAgent();
        if (actions.size() > 0)
            return getBiasedActionForSeekerConfidently(actions, state.getPlayersOnBoard());
        else
            return null;

    }

    private static Action getBiasedActionForHiderConfidently(List<Action> actions, PlayersOnBoard playersOnBoard) {
        return actions.stream()
                .max((action1, action2) -> Integer.compare(
                        playersOnBoard.shortestDistanceBetweenPositionAndClosestSeeker(action1.getDestination()),
                        playersOnBoard.shortestDistanceBetweenPositionAndClosestSeeker(action2.getDestination())))
                .get();
    }

    private static Action getBiasedActionForSeekerConfidently(List<Action> actions, PlayersOnBoard playersOnBoard) {
        return actions.stream()
                .min((action1, action2) -> Integer.compare(
                        playersOnBoard.shortestDistanceBetweenPositionAndHidersMostProbablePosition(
                                action1.getDestination()),
                        playersOnBoard.shortestDistanceBetweenPositionAndHidersMostProbablePosition(
                                action2.getDestination())))
                .get();
    }
}