package io.github.nejc92.sy.game.board;

import org.jgrapht.graph.DefaultEdge;

public class Connection extends DefaultEdge {

    public enum Transportation {
        TAXI, BUS, UNDERGROUND, BLACK_FARE
    }

    private final int vertex1;
    private final int vertex2;
    private final Transportation transportation;

    public Connection(int vertex1, int vertex2, Transportation transportation) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.transportation = transportation;
    }

    public int getVertex1() {
        return vertex1;
    }

    public int getVertex2() {
        return vertex2;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public boolean isTransportation(Transportation transportation) {
        return this.transportation == transportation;
    }
}