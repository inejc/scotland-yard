package io.github.nejc92.sy.players;

import io.github.nejc92.mcts.MctsDomainAgent;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;

import java.util.Collections;
import java.util.List;

public abstract class Player implements MctsDomainAgent<State> {

    public enum Type {
        HIDER, SEEKER
    }

    private final Type type;
    private int boardPosition;
    private int taxiTickets;
    private int busTickets;
    private int undergroundTickets;

    protected Player(Type type, int boardPosition, int taxiTickets, int busTickets, int undergroundTickets) {
        this.type = type;
        this.boardPosition = boardPosition;
        this.taxiTickets = taxiTickets;
        this.busTickets = busTickets;
        this.undergroundTickets = undergroundTickets;
    }

    public Type getType() {
        return type;
    }

    protected int getBoardPosition() {
        return boardPosition;
    }

    public void moveToBoardPosition(int newBoardPosition) {
        boardPosition = newBoardPosition;
    }

    protected int getTaxiTickets() {
        return taxiTickets;
    }

    protected int getBusTickets() {
        return busTickets;
    }

    protected int getUndergroundTickets() {
        return undergroundTickets;
    }

    protected void addTaxiTicket() {
        taxiTickets += 1;
    }

    protected void addBusTicket() {
        busTickets += 1;
    }

    protected void addUndergroundTicket() {
        undergroundTickets += 1;
    }

    public void removeTaxiTicket() {
        taxiTickets -= 1;
    }

    public void removeBusTicket() {
        busTickets -= 1;
    }

    public void removeUndergroundTicket() {
        undergroundTickets -= 1;
    }

    public boolean hasTaxiTickets() {
        return taxiTickets > 0;
    }

    public boolean hasBusTickets() {
        return busTickets > 0;
    }

    public boolean hasUndergroundTickets() {
        return undergroundTickets > 0;
    }

    @Override
    public final State getTerminalStateByPerformingSimulationFromState(State state) {
        while (!state.isTerminal()) {
            Action randomAction = getActionFromStatesAvailableActions(state);
            state.performActionForCurrentAgent(randomAction);
        }
        return state;
    }

    protected Action getActionFromStatesAvailableActions(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
        Collections.shuffle(availableActions);
        return availableActions.get(0);
    }
}