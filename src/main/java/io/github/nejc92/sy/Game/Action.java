package io.github.nejc92.sy.game;

public class Action {

    public enum Transportation {
        TAXI, BUS, UNDERGROUND, BOAT
    }

    public Transportation transportation;
    public int destination;
}