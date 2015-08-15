package io.github.nejc92.sy.players;

        import io.github.nejc92.sy.game.State;

public class RandomSeekerNoCoalitionReduction extends RandomSeeker {

    public RandomSeekerNoCoalitionReduction(Operator operator, Seeker.Color color) {
        super(operator, color);
    }

    @Override
    public double getRewardFromTerminalState(State state) {
        if (state.seekersWon())
            return 1;
        else
            return 0;
    }
}