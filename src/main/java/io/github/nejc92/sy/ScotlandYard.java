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
        Mcts<State, Action, Player> mcts = Mcts.initializeIterations(20000);
        mcts.dontClone(Board.class);
        Player[] players = initializePlayers();
        State state = State.initialize(players);
        while (!state.isTerminal()) {
            state.printNewRound();
            System.out.println("Current player: " + state.getCurrentAgent());
            System.out.print("Positions: ");
            state.printPositions();
            if (state.getAvailableActionsForCurrentAgent().size() > 0) {
                state.setSearchModeOn();
                Action mostPromisingAction = mcts.uctSearchWithExploration(state, 0.4);
                System.out.println(mostPromisingAction);
                state.setSearchModeOff();
                state.performActionForCurrentAgent(mostPromisingAction);
            }
            else
                state.skipCurrentAgent();
            System.out.print("New positions: ");
            state.printPositions();
            System.out.println();
        }
        if (state.seekersWon())
            System.out.println("Seekers won!");
        else
            System.out.println("Hider won!");
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