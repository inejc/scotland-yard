package io.github.nejc92.sy;

import io.github.nejc92.mcts.Mcts;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.board.Board;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.players.RandomHider;
import io.github.nejc92.sy.players.RandomSeeker;
import io.github.nejc92.sy.players.Seeker;

public class ScotlandYard {

    public static void main(String... args) {
        Mcts<State, Action, Player> mcts = Mcts.initializeIterations(500);
        mcts.dontClone(Board.class);
        Player[] players = initializePlayers();
        State state = State.initialize(players);
        while (!state.isTerminal()) {
            state.setSearchModeOn();
            Action mostPromisingAction = mcts.uctSearchWithExploration(state, 0.4);
            state.setSearchModeOff();
            state.performActionForCurrentAgent(mostPromisingAction);
        }
    }

    private static Player[] initializePlayers() {
        Player[] players = new Player[6];
        players[0] = new RandomHider(Player.Operator.COMPUTER);
        players[1] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.BLACK);
        players[2] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.BLUE);
        players[3] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.YELLOW);
        players[4] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.RED);
        players[5] = new RandomSeeker(Player.Operator.COMPUTER, Seeker.Color.GREEN);
        return players;
    }
}