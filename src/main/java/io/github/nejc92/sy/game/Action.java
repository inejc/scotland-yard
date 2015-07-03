package io.github.nejc92.sy.game;

import io.github.nejc92.sy.game.board.Connection;

public class Action {

    private final Connection.Transportation transportation;
    private final int destination;

    public Action(Connection.Transportation transportation, int destination) {
        this.transportation = transportation;
        this.destination = destination;
    }

    public Connection.Transportation getTransportation() {
        return transportation;
    }

    public int getDestination() {
        return destination;
    }

    public boolean isTransportationAction(Connection.Transportation transportation) {
        return this.transportation == transportation;
    }

    protected Action generateBlackFareAction() {
        return new Action(Connection.Transportation.BLACK_FARE, this.destination);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Action action = (Action) object;
        return destination == action.destination && transportation == action.transportation;
    }

    @Override
    public int hashCode() {
        int result = transportation.hashCode();
        result = 31 * result + destination;
        return result;
    }
}