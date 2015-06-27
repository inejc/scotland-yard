package io.github.nejc92.sy.players;

import io.github.nejc92.sy.Game.State;

public class RandomSeeker extends Player {

    public enum Color {
        BLUE, YELLOW, RED, GREEN, BLACK
    }

    private Color color;

    public RandomSeeker(Color color, int boardPosition) {
        super(Type.SEEKER, boardPosition);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        return 0;
    }
}