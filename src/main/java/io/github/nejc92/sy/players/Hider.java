package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.PlayersOnBoard;
import io.github.nejc92.sy.game.State;

import java.util.List;

public abstract class Hider extends Player {

    private static final int TAXI_TICKETS = 4;
    private static final int BUS_TICKETS = 3;
    private static final int UNDERGROUND_TICKETS = 3;

    private int doubleMoveCards;
    private int blackFareTickets;

    public Hider(Operator operator) {
        super(operator, Type.HIDER, TAXI_TICKETS, BUS_TICKETS, UNDERGROUND_TICKETS);
        this.doubleMoveCards = 2;
        this.blackFareTickets = 5;
    }

    public void removeDoubleMoveCard() {
        doubleMoveCards--;
    }

    public void removeBlackFareTicket() {
        blackFareTickets--;
    }

    public boolean hasDoubleMoveCard() {
        return doubleMoveCards > 0;
    }

    public boolean hasBlackFareTicket() {
        return blackFareTickets > 0;
    }

    @Override
    public void addTicket(Action.Transportation transportation) {
        super.addTicket(transportation);
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        if (state.hiderWon())
            return 1;
        else
            return 0;
    }

    public boolean shouldUseBlackfareTicket(int currentRound, List<Action> actions) {
        return hasBlackFareTicket() && optimalToUseBlackFareTicket(currentRound, actions);
    }

    protected abstract boolean optimalToUseBlackFareTicket(int currentRound, List<Action> actions);

    public boolean shouldUseDoubleMove(PlayersOnBoard playersOnBoard) {
        return hasDoubleMoveCard() && optimalToUseDoubleMoveCard(playersOnBoard);
    }

    protected abstract boolean optimalToUseDoubleMoveCard(PlayersOnBoard playersOnBoard);

    @Override
    public String toString() {
        return "Hider";
    }
}