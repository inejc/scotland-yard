package io.github.nejc92.sy;

import io.github.nejc92.mcts.Mcts;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.Board;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScotlandYard {

    private static final List<Integer> STARTING_POSITIONS = new ArrayList<>(
        Arrays.asList(13, 26, 34, 50, 53, 62, 91, 94, 103, 112, 117, 132, 138, 141, 155, 174, 197, 198));

    public static void main(String... args) {
        Mcts<State, Action, Player> mcts = Mcts.initializeIterations(500);
        mcts.dontClone(Board.class);
        Player[] players = initializePlayers();
        State state = State.initialize(players, STARTING_POSITIONS);
        while (!state.isTerminal()) {
            Player currentPlayer = state.getCurrentAgent();
            if (currentPlayer.isHuman())
                break;
            else {
                Action mostPromisingAction = mcts.uctSearchWithExploration(state, 0.4);
                state.performActionForCurrentAgent(mostPromisingAction);
            }
        }
    }

    private static Player[] initializePlayers() {
        return null;
    }
}