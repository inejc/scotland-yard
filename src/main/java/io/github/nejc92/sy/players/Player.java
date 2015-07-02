package io.github.nejc92.sy.players;

import io.github.nejc92.mcts.MctsDomainAgent;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;

public abstract class Player implements MctsDomainAgent<State> {

    public enum Operator {
        HUMAN, COMPUTER
    }

    public enum Type {
        HIDER, SEEKER
    }

    private final Operator operator;
    private final Type type;
    private int taxiTickets;
    private int busTickets;
    private int undergroundTickets;

    protected Player(Operator operator, Type type, int taxiTickets, int busTickets, int undergroundTickets) {
        this.operator = operator;
        this.type = type;
        this.taxiTickets = taxiTickets;
        this.busTickets = busTickets;
        this.undergroundTickets = undergroundTickets;
    }

    public boolean isHider() {
        return type == Type.HIDER;
    }

    public boolean isHuman() {
        return operator == Operator.HUMAN;
    }

    public void removeTicket(Action.Transportation transportation) {
        switch (transportation) {
            case TAXI:
                taxiTickets--;
                break;
            case BUS:
                busTickets--;
                break;
            case UNDERGROUND:
                undergroundTickets--;
        }
    }

    public void addTicket(Action.Transportation transportation) {
        switch (transportation) {
            case TAXI:
                taxiTickets++;
                break;
            case BUS:
                busTickets++;
                break;
            case UNDERGROUND:
                undergroundTickets++;
        }
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
            Action randomAction = getActionFromStatesAvailableActionsForSimulation(state);
            state.performActionForCurrentAgent(randomAction);
        }
        return state;
    }

    protected abstract Action getActionFromStatesAvailableActionsForSimulation(State state);

//    protected Action getActionFromStatesAvailableActionsForSimulation(State state) {
//        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();
//        Collections.shuffle(availableActions);
//        return availableActions.get(0);
//    }
}