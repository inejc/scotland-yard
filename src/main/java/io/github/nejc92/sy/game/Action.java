package io.github.nejc92.sy.game;

public class Action {

    public enum Transportation {
        TAXI, BUS, UNDERGROUND, BOAT
    }

    private final Transportation transportation;
    private final int destination;

    public Action(Transportation transportation, int destination) {
        this.transportation = transportation;
        this.destination = destination;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public int getDestination() {
        return destination;
    }
}