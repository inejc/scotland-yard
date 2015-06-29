package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.State;

public class RandomHider extends Player {

    private static final int TAXI_TICKETS = 4;
    private static final int BUS_TICKETS = 3;
    private static final int UNDERGROUND_TICKETS = 3;

    private int doubleMoveCards;
    private int blackFareCards;

    public RandomHider(int boardPosition) {
        super(Type.HIDER, boardPosition, TAXI_TICKETS, BUS_TICKETS, UNDERGROUND_TICKETS);
        this.doubleMoveCards = 2;
        this.blackFareCards = 5;
    }

    public boolean isOnPosition(int boardPosition) {
        return this.getBoardPosition() == boardPosition;
    }

    public void removeDoubleMoveCard() {
        doubleMoveCards--;
    }

    public void removeBlackFareCard() {
        blackFareCards--;
    }

    public boolean hasDoubleMoveCard() {
        return doubleMoveCards > 0;
    }

    public boolean hasBlackFareCard() {
        return blackFareCards > 0;
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