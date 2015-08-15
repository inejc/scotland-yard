package io.github.nejc92.sy.players;

public abstract class Seeker extends Player {

    private static final int TAXI_TICKETS = 10;
    private static final int BUS_TICKETS = 8;
    private static final int UNDERGROUND_TICKETS = 4;

    public enum Color {
        BLACK, BLUE, YELLOW, RED, GREEN
    }

    private final Color color;

    public Seeker(Operator operator, Color color) {
        super(operator, Type.SEEKER, TAXI_TICKETS, BUS_TICKETS, UNDERGROUND_TICKETS);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seeker seeker = (Seeker) o;
        return color == seeker.color;
    }

    @Override
    public int hashCode() {
        return color != null ? color.hashCode() : 0;
    }

    @Override
    public String toString() {
        return color + " Seeker";
    }
}