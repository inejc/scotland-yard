package io.github.nejc92.sy.players;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.strategies.CoalitionReduction;
import io.github.nejc92.sy.strategies.MoveFiltering;
import io.github.nejc92.sy.strategies.Playouts;

public class Seeker extends Player {

    private static final int TAXI_TICKETS = 10;
    private static final int BUS_TICKETS = 8;
    private static final int UNDERGROUND_TICKETS = 4;
    private static final double COALITION_REDUCTION_PARAMETER = 0.25;

    public Seeker(Operator operator, String name, Playouts.Uses playout, CoalitionReduction.Uses coalitionReduction,
                  MoveFiltering.Uses moveFiltering) {
        super(operator, Type.SEEKER, name, TAXI_TICKETS, BUS_TICKETS, UNDERGROUND_TICKETS, playout,
                coalitionReduction, moveFiltering);
    }

    @Override
    protected Action getActionForHiderFromStatesAvailableActionsForSimulation(State state) {
        if (this.usesBiasedPlayout())
            return Playouts.getGreedyBiasedActionForHider(state);
        else
            return Playouts.getRandomAction(state);
    }

    @Override
    protected Action getActionForSeekerFromStatesAvailableActionsForSimulation(State state) {
        if (this.usesBiasedPlayout())
            return Playouts.getGreedyBiasedActionForSeeker(state);
        else
            return Playouts.getRandomAction(state);
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        if (state.searchInvokingPlayerUsesCoalitionReduction())
            return CoalitionReduction.getCoalitionReductionRewardFromTerminalState(state, this);
        else
            return CoalitionReduction.getNormalRewardFromTerminalState(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seeker seeker = (Seeker) o;
        return this.name.equals(seeker.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}