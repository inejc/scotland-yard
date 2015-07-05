package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.board.Connection;
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

    public void removeBlackFareTickets() {
        blackFareTickets--;
    }

    public boolean hasDoubleMoveCard() {
        return doubleMoveCards > 0;
    }

    public boolean hasBlackFareTicket() {
        return blackFareTickets > 0;
    }

    @Override
    public void addTicket(Connection.Transportation transportation) {
        super.addTicket(transportation);
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        return 0;
    }

    public boolean shouldUseBlackfareTicket(int currentRound, List<Action> actions) {
        return false;
    }

    public boolean shouldUseDoubleMove() {
        return false;
    }
}