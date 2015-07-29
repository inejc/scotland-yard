package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;
import io.github.nejc92.sy.game.State;

import java.util.List;

public class BiasedSeeker extends Seeker {

    public BiasedSeeker(Operator operator, Seeker.Color color) {
        super(operator, color);
    }

    @Override
    protected Action getActionFromStatesAvailableActionsForSimulation(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        if (availableActions.size() > 0)
            return getBiasedForSeekerActionFromActions(availableActions, state.getPlayersOnBoard());
        else
            return null;
    }

//    private Action getBiasedForSeekerActionFromActions(List<Action> actions, PlayersOnBoard playersOnBoard) {
//        return actions.stream()
//                .min((action1, action2) -> Integer.compare(
//                        playersOnBoard.sumDistancesFromPositionToAllPossibleHidersPositions(action1.getDestination()),
//                        playersOnBoard.sumDistancesFromPositionToAllPossibleHidersPositions(action2.getDestination())))
//                .get();
//    }

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