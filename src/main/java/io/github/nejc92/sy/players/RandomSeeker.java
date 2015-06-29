package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.State;

public class RandomSeeker extends Player {

    private static final int TAXI_TICKETS = 10;
    private static final int BUS_TICKETS = 8;
    private static final int UNDERGROUND_TICKETS = 4;

    public enum Color {
        BLUE, YELLOW, RED, GREEN, BLACK
    }

    private final Color color;

    public RandomSeeker(int boardPosition, Color color) {
        super(Type.SEEKER, boardPosition, TAXI_TICKETS, BUS_TICKETS, UNDERGROUND_TICKETS);
        this.color = color;
    }

    @Override
    public int getBoardPosition() {
        return super.getBoardPosition();
    }

    public Color getColor() {
        return color;
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        return 0;
    }
}