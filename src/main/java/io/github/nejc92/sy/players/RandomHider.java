package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;

public class RandomHider extends Player {

    private static final int TAXI_TICKETS = 4;
    private static final int BUS_TICKETS = 3;
    private static final int UNDERGROUND_TICKETS = 3;

    public RandomHider(int boardPosition) {
        super(Type.HIDER, boardPosition, TAXI_TICKETS, BUS_TICKETS, UNDERGROUND_TICKETS);
    }

    public boolean isOnPosition(int boardPosition) {
        return this.getBoardPosition() == boardPosition;
    }

    public void addTicket(Action.Transportation transportation) {
        super.addTicket(transportation);
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        return 0;
    }
}