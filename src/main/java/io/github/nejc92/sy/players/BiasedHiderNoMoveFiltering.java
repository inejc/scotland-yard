package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;

import java.util.List;

public class BiasedHiderNoMoveFiltering extends BiasedHider {

    public BiasedHiderNoMoveFiltering(Operator operator) {
        super(operator);
    }

    @Override
    protected boolean optimalToUseBlackFareTicket(int currentRound, List<Action> actions) {
        return true;
    }

    @Override
    protected boolean optimalToUseDoubleMoveCard(PlayersOnBoard playersOnBoard) {
        return true;
    }
}