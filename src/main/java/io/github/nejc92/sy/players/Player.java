package io.github.nejc92.sy.players;

import io.github.nejc92.mcts.MctsDomainAgent;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.strategies.CoalitionReduction;
import io.github.nejc92.sy.strategies.MoveFiltering;
import io.github.nejc92.sy.strategies.Playouts;

public abstract class Player implements MctsDomainAgent<State> {
    
    public enum Operator {
        HUMAN, MCTS, RANDOM
    }

    public enum Type {
        HIDER, SEEKER;

        @Override
        public String toString() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    public enum Color {
        BLACK, BLUE, YELLOW, RED, GREEN, WHITE, ORANGE
    }

    protected final String name;
    private final Operator operator;
    private final Type type;
    private int taxiTickets;
    private int busTickets;
    private int undergroundTickets;
    private final Playouts.Uses playout;
    private final CoalitionReduction.Uses coalitionReduction;
    private final MoveFiltering.Uses moveFiltering;

    protected Player(Operator operator, Type type, String name, int taxiTickets, int busTickets, int undergroundTickets,
                     Playouts.Uses playout, CoalitionReduction.Uses coalitionReduction,
                     MoveFiltering.Uses moveFiltering) {
        this.operator = operator;
        this.type = type;
        this.name = name;
        this.taxiTickets = taxiTickets;
        this.busTickets = busTickets;
        this.undergroundTickets = undergroundTickets;
        this.playout = playout;
        this.coalitionReduction = coalitionReduction;
        this.moveFiltering = moveFiltering;
    }

    public int getTaxiTickets() {
        return taxiTickets;
    }

    public int getBusTickets() {
        return busTickets;
    }

    public int getUndergroundTickets() {
        return undergroundTickets;
    }

    public boolean isHider() {
        return type == Type.HIDER;
    }

    public boolean isSeeker() {
        return type == Type.SEEKER;
    }

    public boolean isHuman() {
        return operator == Operator.HUMAN;
    }

    public boolean isRandom() {
        return operator == Operator.RANDOM;
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

    protected void addTicket(Action.Transportation transportation) {
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

    @Override
    public final State getTerminalStateByPerformingSimulationFromState(State state) {
        while (!state.isTerminal()) {
            Action action = getActionForCurrentPlayerType(state);
            if (action != null) {
                state.performActionForCurrentAgent(action);
            }
            else
                state.skipCurrentAgent();
        }
        return state;
    }

    public boolean usesBiasedPlayout () {
        switch (playout) {
            case BASIC:
                return false;
            default:
                return true;
        }
    }

    public boolean usesCoalitionReduction() {
        switch (coalitionReduction) {
            case YES:
                return true;
            default:
                return false;
        }
    }

    public boolean usesMoveFiltering() {
        switch (moveFiltering) {
            case YES:
                return true;
            default:
                return false;
        }
    }

    private Action getActionForCurrentPlayerType(State state) {
        if (state.currentPlayerIsHider())
            return getActionForHiderFromStatesAvailableActionsForSimulation(state);
        else
            return getActionForSeekerFromStatesAvailableActionsForSimulation(state);
    }

    protected abstract Action getActionForHiderFromStatesAvailableActionsForSimulation(State state);

    protected abstract Action getActionForSeekerFromStatesAvailableActionsForSimulation(State state);

    @Override
    public String toString() {
        
        return String.format("%s '%s'", type.toString(), name);
    }
}