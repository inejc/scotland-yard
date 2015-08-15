package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BiasedHiderMoveFiltering extends BiasedHider {

    private static final List<Integer> SHOULDNT_USE_BLACK_FAIR_ROUNDS = new ArrayList<>(
            Arrays.asList(1, 2, 3, 8, 13, 18, 24)
    );
    private static final double SHOULD_USE_DOUBLE_MOVE_AVG_DISTANCE_THRESHOLD = 2.5;

    public BiasedHiderMoveFiltering(Operator operator) {
        super(operator);
    }

    @Override
    protected boolean optimalToUseBlackFareTicket(int currentRound, List<Action> actions) {
        return !SHOULDNT_USE_BLACK_FAIR_ROUNDS.contains(currentRound) && !actionsContainOnlyTaxis(actions);
    }

    private boolean actionsContainOnlyTaxis(List<Action> actions) {
        return actions.stream().allMatch(action -> action.isTransportationAction(Action.Transportation.TAXI));
    }

    @Override
    protected boolean optimalToUseDoubleMoveCard(PlayersOnBoard playersOnBoard) {
        return playersOnBoard.hidersAverageDistanceToSeekers(Type.SEEKER)
                <= SHOULD_USE_DOUBLE_MOVE_AVG_DISTANCE_THRESHOLD;
    }
}