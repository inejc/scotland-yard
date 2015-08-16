package io.github.nejc92.sy.strategies;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;
import io.github.nejc92.sy.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoveFiltering {

    public enum Uses {
        YES, NO
    }

    private static final List<Integer> SHOULDNT_USE_BLACK_FAIR_ROUNDS = new ArrayList<>(
            Arrays.asList(1, 2, 3, 8, 13, 18, 24)
    );
    private static final double SHOULD_USE_DOUBLE_MOVE_AVG_DISTANCE_THRESHOLD = 3;

    public static boolean optimalToUseBlackFareTicket(int currentRound, List<Action> actions) {
        return !SHOULDNT_USE_BLACK_FAIR_ROUNDS.contains(currentRound) && !actionsContainOnlyTaxis(actions);
    }

    private static boolean actionsContainOnlyTaxis(List<Action> actions) {
        return actions.stream().allMatch(action -> action.isTransportationAction(Action.Transportation.TAXI));
    }

    public static boolean optimalToUseDoubleMoveCard(PlayersOnBoard playersOnBoard) {
        return playersOnBoard.hidersAverageDistanceToSeekers(Player.Type.SEEKER)
                <= SHOULD_USE_DOUBLE_MOVE_AVG_DISTANCE_THRESHOLD;
    }
}