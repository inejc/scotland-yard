package io.github.nejc92.sy;

import io.github.nejc92.mcts.Mcts;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.Board;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.players.*;

import java.util.List;
import java.util.Scanner;

public class ScotlandYard {

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        Mcts<State, Action, Player> mcts = initializeSearch();
        Player[] players = initializePlayers();
        State state = State.initialize(players);
        while (!state.isTerminal()) {
            performOneAction(state, mcts, scanner);
        }
        printResult(state);
    }

    private static Mcts<State, Action, Player> initializeSearch() {
        Mcts<State, Action, Player> mcts = Mcts.initializeIterations(10000);
        mcts.dontClone(Board.class);
        return mcts;
    }

    private static Player[] initializePlayers() {
        Player[] players = new Player[6];
        players[0] = new BiasedHider(Player.Operator.COMPUTER);
        players[1] = new BiasedSeeker(Player.Operator.COMPUTER, Seeker.Color.BLACK);
        players[2] = new BiasedSeeker(Player.Operator.COMPUTER, Seeker.Color.BLUE);
        players[3] = new BiasedSeeker(Player.Operator.COMPUTER, Seeker.Color.YELLOW);
        players[4] = new BiasedSeeker(Player.Operator.COMPUTER, Seeker.Color.RED);
        players[5] = new BiasedSeeker(Player.Operator.COMPUTER, Seeker.Color.GREEN);
        return players;
    }

    private static void performOneAction(State state, Mcts<State, Action, Player> mcts, Scanner scanner) {
        //printBeforeMove(state);
        if (state.getAvailableActionsForCurrentAgent().size() > 0) {
            Action mostPromisingAction = getNextAction(state, mcts, scanner);
            state.performActionForCurrentAgent(mostPromisingAction);
        }
        else {
            state.skipCurrentAgent();
        }
        askHumanForDoubleMove(state, scanner);
    }

    private static void printBeforeMove(State state) {
        state.printNewRound();
        System.out.println();
        System.out.println("Current player: " + state.getCurrentAgent());
        state.printAllPositions();
    }

    private static Action getNextAction(State state, Mcts<State, Action, Player> mcts, Scanner scanner) {
        Action mostPromisingAction;
        if (state.currentPlayerIsHuman()) {
            mostPromisingAction = getActionFromInput(state, scanner);
        }
        else {
            mostPromisingAction = getActionFromSearch(state, mcts);
        }
        return mostPromisingAction;
    }

    private static Action getActionFromInput(State state, Scanner scanner) {
        System.out.println("Available actions:");
        printAvailableActions(state.getAvailableActionsForCurrentAgent());
        System.out.print("Enter action: ");
        int action = Integer.parseInt(scanner.nextLine());
        return state.getAvailableActionsForCurrentAgent().get(action);
    }

    private static Action getActionFromSearch(State state, Mcts<State, Action, Player> mcts) {
        Action mostPromisingAction;
        state.setSearchModeOn();
        if (state.isTerminal())
            state.fixHidersProbablePosition();
        if (state.currentPlayerIsHider())
            mostPromisingAction = mcts.uctSearchWithExploration(state, 0.2);
        else
            mostPromisingAction = mcts.uctSearchWithExploration(state, 2);
        //printSelectedAction(mostPromisingAction);
        state.setSearchModeOff();
        return mostPromisingAction;
    }

    private static void printSelectedAction(Action action) {
        System.out.println(action);
        System.out.println();
    }

    private static void askHumanForDoubleMove(State state, Scanner scanner) {
        if (state.previousPlayerIsHider() && state.previousPlayerIsHuman()) {
            Hider hider = (Hider)state.getPreviousAgent();
            if (hider.hasDoubleMoveCard()) {
                System.out.println("Use double move? y/n");
                String doubleMove = scanner.nextLine();
                if (doubleMove.equals("y")) {
                    state.skipAllSeekers();
                    hider.removeDoubleMoveCard();
                }
            }
        }
    }

    private static void printAvailableActions(List<Action> actions) {
        for (int i = 0; i < actions.size(); i++) {
            System.out.println(i + ": " + actions.get(i));
        }
        System.out.println();
    }

    private static void printResult(State state) {
        if (state.seekersWon())
            System.out.println("Seekers won!");
        else
            System.out.println("Hider won!");
    }
}