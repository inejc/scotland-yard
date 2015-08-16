package io.github.nejc92.sy.strategies;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;
import io.github.nejc92.sy.game.State;

import java.util.Collections;
import java.util.List;

public class Playouts {

    public enum Uses {
        RANDOM, BIASED
    }

    private static final double EPSILON = 0.2;

    public static Action getRandomAction(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        Collections.shuffle(availableActions);
        if (availableActions.size() > 0)
            return availableActions.get(0);
        else
            return null;
    }

    public static Action getGreedyBiasedActionForHider(State state) {
        List<Action> actions = state.getAvailableActionsForCurrentAgent();
        if (actions.size() > 0) {
            if (shouldReturnBiasedAction())
                return getBiasedActionForHiderConfidently(actions, state.getPlayersOnBoard());
            else
                return getRandomAction(state);
        }
        else
            return null;
    }

    public static Action getGreedyBiasedActionForSeeker(State state) {
        List<Action> actions = state.getAvailableActionsForCurrentAgent();
        if (actions.size() > 0) {
            if (shouldReturnBiasedAction())
                return getBiasedActionForSeekerConfidently(actions, state.getPlayersOnBoard());
            else
                return getRandomAction(state);
        }
        else
            return null;

    }

    private static boolean shouldReturnBiasedAction() {
        return Math.random() > EPSILON;
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