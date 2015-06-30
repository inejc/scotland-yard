package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.State;

public abstract class Hider extends Player {

    private static final int TAXI_TICKETS = 4;
    private static final int BUS_TICKETS = 3;
    private static final int UNDERGROUND_TICKETS = 3;

    private int doubleMoveCards;
    private int blackFareTickets;

    public Hider(Operator operator, int boardPosition) {
        super(operator, Type.HIDER, boardPosition, TAXI_TICKETS, BUS_TICKETS, UNDERGROUND_TICKETS);
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
    public void addTaxiTicket() {
        super.addTaxiTicket();
    }

    @Override
    public void addBusTicket() {
        super.addBusTicket();
    }

    @Override
    public void addUndergroundTicket() {
        super.addUndergroundTicket();
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        return 0;
    }
}